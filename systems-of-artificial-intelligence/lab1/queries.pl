:- encoding(utf8).
:- use_module(knowledge_base).

% Вспомогательный файл демонстрации; не входит в счётчик правил БЗ.
% Вывод сохраняет реальные решения SWI-Prolog, а не заранее записанные ответы.
show_boolean(Text, Goal) :-
    format('?- ~s~n', [Text]),
    (call(Goal) -> writeln('true.'); writeln('false.')), nl.

show_set(Text, Template, Goal) :-
    format('?- ~s~n', [Text]),
    (setof(Template, Goal, Answers) ->
        format('~q.~n', [Answers]); writeln('false.')), nl.

main :-
    current_prolog_flag(version_data, Version),
    format('Interpreter: ~q~n~n', [Version]),
    show_boolean("game(rune_quest).", game(rune_quest)),
    show_boolean("game(unknown_game).", game(unknown_game)),
    show_set("setof(G, rpg(G), Games).", G, rpg(G)),
    show_set("setof(G, (indie(G), cooperative(G)), Games).",
             G, (indie(G), cooperative(G))),
    show_set("setof(G, (rpg(G); puzzle(G)), Games).",
             G, (rpg(G); puzzle(G))),
    show_set("setof(G, (game(G), \\+ cooperative(G)), Games).",
             G, (game(G), \+ cooperative(G))),
    show_set("setof(P, adult(P), Players).", P, adult(P)),
    show_set("setof(G, eligible(vera, G), Games).", G, eligible(vera, G)),
    show_set("setof(P-G, recommended(P, G), Pairs).",
             P-G, recommended(P, G)),
    show_set("setof(G, can_play_together(anna, boris, G), Games).",
             G, can_play_together(anna, boris, G)).
