:- encoding(utf8).
:- use_module(library(plunit)).
:- use_module(knowledge_base).

:- begin_tests(knowledge_base).

test(imperial, set(C == [mephiston, roboute_guilliman])) :- imperial_character(C).
test(chaos, set(C == [abaddon, ahriman])) :- chaos_character(C).
test(xenos, set(C == [eldrad_ulthran, imotekh])) :- xenos_character(C).
test(unknown_character, [fail]) :- imperial_character(unknown_character).
test(faction_not_character, [fail]) :- imperial_character(ultramarines).
test(intersection, set(C == [ahriman])) :- psyker(C), commander(C).
test(union, set(C == [abaddon, ahriman, eldrad_ulthran, imotekh, mephiston, roboute_guilliman])) :- (psyker(C); commander(C)).
test(dangerous, set(C == [abaddon, ahriman, eldrad_ulthran, imotekh, roboute_guilliman])) :- dangerous(C).
test(threshold_inclusive) :- dangerous(imotekh).
test(low_rating, [fail]) :- dangerous(mephiston).
test(unknown_not_dangerous, [fail]) :- dangerous(unknown_character).
test(non_psykers, set(C == [abaddon, imotekh, roboute_guilliman])) :- non_psyker(C).
test(psyker_not_non_psyker, [fail]) :- non_psyker(ahriman).
test(unknown_not_non_psyker, [fail]) :- non_psyker(unknown_character).
test(enemy_forward, [nondet]) :- enemies(roboute_guilliman, abaddon).
test(enemy_reverse) :- enemies(abaddon, roboute_guilliman).
test(enemy_xenos) :- enemies(imotekh, eldrad_ulthran).
test(enemy_set, set(C == [ahriman])) :- enemies(mephiston, C).
test(no_self_enemy, [fail]) :- enemies(ahriman, ahriman).
test(no_invented_conflict, [fail]) :- enemies(abaddon, mephiston).
test(imperial_alliance, [nondet]) :- allies(roboute_guilliman, mephiston).
test(chaos_alliance) :- allies(abaddon, ahriman).
test(alliance_reverse) :- allies(ahriman, abaddon).
test(no_self_ally, [fail]) :- allies(ahriman, ahriman).
test(xenos_not_one_alliance, [fail]) :- allies(eldrad_ulthran, imotekh).
test(opposing_sides, [fail]) :- allies(abaddon, roboute_guilliman).
test(alliance_pairs, set(A-B == [abaddon-ahriman, ahriman-abaddon, mephiston-roboute_guilliman, roboute_guilliman-mephiston])) :- allies(A, B).
test(no_ally_enemy_overlap, [fail]) :- allies(A, B), enemies(A, B).

% Интроспекция экспортированной БЗ: 25 унарных, 15 бинарных фактов и 7 правил.
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
