:- encoding(utf8).
:- use_module(knowledge_base).

show_boolean(Text, Goal) :-
    format('?- ~s~n', [Text]),
    (call(Goal) -> writeln('true.'); writeln('false.')), nl.

show_set(Text, Template, Goal) :-
    format('?- ~s~n', [Text]),
    (setof(Template, Goal, Answers) ->
        format('~q.~n', [Answers]); writeln('false.')), nl.

main :-
    current_prolog_flag(version_data, Version),
    format('Warhammer 40,000 | Interpreter: ~q~n~n', [Version]),
    show_boolean("character(ahriman).", character(ahriman)),
    show_boolean("character(unknown_character).", character(unknown_character)),
    show_set("setof(C, psyker(C), Characters).", C, psyker(C)),
    show_set("setof(C, (psyker(C), commander(C)), Characters).",
             C, (psyker(C), commander(C))),
    show_set("setof(C, (psyker(C); commander(C)), Characters).",
             C, (psyker(C); commander(C))),
    show_set("setof(C, (character(C), \\+ psyker(C)), Characters).",
             C, (character(C), \+ psyker(C))),
    show_set("setof(C, imperial_character(C), Characters).", C, imperial_character(C)),
    show_set("setof(C, dangerous(C), Characters).", C, dangerous(C)),
    show_set("setof(C, enemies(abaddon, C), Characters).", C, enemies(abaddon, C)),
    show_set("setof(C, allies(ahriman, C), Characters).", C, allies(ahriman, C)).
