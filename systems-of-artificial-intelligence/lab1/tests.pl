:- encoding(utf8).
:- use_module(library(plunit)).
:- use_module(knowledge_base).

:- begin_tests(knowledge_base).

% Проверка результата каждого правила и важных граничных случаев.
test(adults, set(P == [anna])) :- adult(P).
test(age_boundary_allowed) :- eligible(boris, rune_quest).
test(too_young, [fail]) :- eligible(boris, shadow_realm).
test(zero_age_game) :- eligible(vera, logic_garden).
test(youngest_games, set(G == [logic_garden, star_fleet])) :- eligible(vera, G).
test(unknown_player, [fail]) :- eligible(nobody, logic_garden).
test(unknown_game, [fail]) :- eligible(anna, unknown_game).
test(genre_matches, set(G == [empire_builder, star_fleet])) :- matches_genre(boris, G).
test(recommendations, set(P-G == [anna-shadow_realm, boris-star_fleet, vera-logic_garden])) :- recommended(P, G).
test(owned_not_recommended, [fail]) :- recommended(anna, rune_quest).
test(wrong_genre, [fail]) :- recommended(anna, star_fleet).
test(age_blocks_recommendation, [fail]) :- recommended(boris, shadow_realm).
test(social_union, set(G == [empire_builder, rune_quest, star_fleet])) :- social_game(G).
test(reverse_friend) :- friends(boris, anna).
test(no_self_friend, [fail]) :- friends(anna, anna).
test(friendship_not_transitive, [fail]) :- friends(anna, vera).
test(together, set(G == [empire_builder, rune_quest, star_fleet])) :- can_play_together(anna, boris, G).
test(together_age_boundary, set(G == [star_fleet])) :- can_play_together(boris, vera, G).
test(together_symmetry, set(G == [empire_builder, rune_quest, star_fleet])) :- can_play_together(boris, anna, G).
test(strangers_cannot_play, [fail]) :- can_play_together(anna, vera, star_fleet).
test(singleplayer_blocked, [fail]) :- can_play_together(anna, boris, logic_garden).

% Проверяем объём исходной БЗ через её клаузы, без встроенных предикатов.
test(required_size) :-
    findall(A, (current_predicate(knowledge_base:Name/A),
                functor(Head, Name, A),
                predicate_property(knowledge_base:Head, exported),
                clause(knowledge_base:Head, true)), Arities),
    include(=(1), Arities, Unary), length(Unary, 25),
    include(=(2), Arities, Binary), length(Binary, 15),
    findall(Head, (current_predicate(knowledge_base:Name/A),
                  functor(Head, Name, A),
                  predicate_property(knowledge_base:Head, exported),
                  clause(knowledge_base:Head, Body), Body \== true), Rules),
    length(Rules, 7).

:- end_tests(knowledge_base).
