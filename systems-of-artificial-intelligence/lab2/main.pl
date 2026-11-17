:- encoding(utf8).
:- module(lab2, [
    parse_profile/2, parse_rating/2, parse_ally/2,
    make_request/4, build_query/3, recommend/2, nearest/2,
    character_name/2, faction_name/2, side_name/2, role_name/2,
    character_side/2, failure_text/2,
    main/0, run_dialog/2, run_dialog/3, run_demo/0, print_help/1
]).
:- use_module('../lab1/knowledge_base').
:- use_module(library(error)).
:- use_module(library(lists)).
:- use_module(library(pairs)).
:- use_module(library(readutil)).
:- initialization(main, main).

% Точка входа. Для работы нужны только SWI-Prolog и соседняя папка lab1.
main :-
    set_stream(user_input, encoding(utf8)),
    set_stream(user_output, encoding(utf8)),
    set_stream(user_error, encoding(utf8)),
    current_prolog_flag(argv, Args),
    ( Args == [] -> run_dialog(user_input, user_output)
    ; Args == ['--show-query'] -> run_dialog(user_input, user_output, [show_query(true)])
    ; Args == ['--demo'] -> run_demo
    ; Args == ['--help'] -> print_help(user_output)
    ; print_help(user_error), halt(2)
    ).

% === Разбор фиксированного формата и нормализация ===
normalized(Text, Normal) :-
    must_be(string, Text),
    string_length(Text, Length),
    (Length =< 512 -> true; input_error("Строка слишком длинная: максимум 512 символов.")),
    normalize_space(string(Trimmed), Text),
    string_lower(Trimmed, Normal).

input_error(Message) :- throw(error(input_error(Message), lab2)).

% parse_profile(+String, -Profile): две секции в заданном порядке.
% Пример: "Сторона: Хаос, ксеносы; роль: не псайкер".
parse_profile(Text, profile{ sides:Sides, role:Role }) :-
    normalized(Text, Normal),
    ( split_string(Normal, ";", " ", [SidePart, RolePart]),
      split_string(SidePart, ":", " ", [SideKey, SideText]),
      memberchk(SideKey, ["сторона", "side"]), SideText \== "",
      split_string(RolePart, ":", " ", [RoleKey, RoleText]),
      memberchk(RoleKey, ["роль", "role"]), RoleText \== ""
    -> true
    ; input_error("Нужен формат: Сторона: Хаос; роль: псайкер")
    ),
    split_string(SideText, ",", " ", SideTokens),
    maplist(parse_side, SideTokens, RawSides), sort(RawSides, Sides),
    (memberchk(any, Sides), Sides \== [any]
    -> input_error("«Любая» уже включает все стороны: укажите её отдельно.")
    ; true),
    parse_role(RoleText, Role).

parse_side(Text, Side) :-
    (side_alias(Text, Side) -> true
    ; input_error("Неизвестная сторона. Доступны: Империум, Хаос, ксеносы, любая." )).

side_alias("империум", imperium).
side_alias("imperium", imperium).
side_alias("хаос", chaos).
side_alias("chaos", chaos).
side_alias("ксеносы", xenos).
side_alias("ксенос", xenos).
side_alias("xenos", xenos).
side_alias("любая", any).
side_alias("any", any).

parse_role(Text, Role) :-
    (memberchk(Text, ["псайкер и не псайкер", "не псайкер и псайкер"])
    -> input_error("Противоречие: роль не может одновременно требовать псайкера и не-псайкера.")
    ; role_alias(Text, Role) -> true
    ; input_error("Неизвестная роль. Варианты: псайкер, военачальник, не псайкер, псайкер и военачальник, псайкер или военачальник, любая.")
    ).

role_alias("псайкер", psyker).
role_alias("psyker", psyker).
role_alias("военачальник", commander).
role_alias("командир", commander).
role_alias("commander", commander).
role_alias("не псайкер", non_psyker).
role_alias("не-псайкер", non_psyker).
role_alias("non_psyker", non_psyker).
role_alias("псайкер и военачальник", psyker_commander).
role_alias("psyker and commander", psyker_commander).
role_alias("псайкер или военачальник", psyker_or_commander).
role_alias("psyker or commander", psyker_or_commander).
role_alias("любая", any).
role_alias("any", any).

% Только десятичные целые 0..10. Enter означает 0.
parse_rating(Text, Rating) :-
    normalized(Text, Normal),
    ( Normal == "" -> Rating = 0
    ; string_codes(Normal, Codes), Codes \== [],
      maplist(decimal_digit, Codes), number_string(Number, Normal),
      integer(Number), between(0, 10, Number)
    -> Rating = Number
    ; input_error("Введите целое число от 0 до 10 или нажмите Enter для 0.")
    ).
decimal_digit(Code) :- between(0'0, 0'9, Code).

% Enter / «нет» отключают ограничение союзника. Иначе нужно известное имя.
parse_ally(Text, Ally) :-
    normalized(Text, Normal),
    ( memberchk(Normal, ["", "нет", "none"]) -> Ally = none
    ; known_character_alias(Normal, Character) -> Ally = Character
    ; input_error("Неизвестный персонаж. Введите имя из списка или Enter без ограничения.")
    ).

known_character_alias("жиллиман", roboute_guilliman).
known_character_alias(Text, Character) :-
    character(Character),
    (atom_string(Character, Text)
    ; character_name(Character, Display), string_lower(Display, Text)).

% === Запрос к импортированной базе знаний ===
make_request(profile{sides:Sides, role:Role}, Minimum, Ally,
             request{sides:Sides, role:Role, min_rating:Minimum, ally:Ally}).

% Проверка программного API. Диалог дополнительно проверяет текстовый ввод.
valid_request(Request) :-
    must_be(dict, Request),
    Sides = Request.sides, Role = Request.role,
    Minimum = Request.min_rating, Ally = Request.ally,
    must_be(list, Sides), Sides \== [],
    maplist(valid_side, Sides),
    (memberchk(any, Sides) -> Sides = [any]; true),
    memberchk(Role, [any, psyker, commander, non_psyker,
                     psyker_commander, psyker_or_commander]),
    must_be(integer, Minimum),
    (between(0, 10, Minimum) -> true
    ; domain_error(threat_rating, Minimum)),
    (Ally == none -> true; character(Ally)).
valid_side(Side) :- memberchk(Side, [any, imperium, chaos, xenos]).

% build_query(+Request, ?Character, -Goal): формирует исполняемый терм.
% character/1 связывает объект до отрицания; стороны объединяются через ИЛИ,
% роль, рейтинг и союзник соединяются через И. Никакой строки с кодом нет.
build_query(Request, Character, Goal) :-
    valid_request(Request),
    side_goal(Request.sides, Character, SideGoal),
    role_goal(Request.role, Character, RoleGoal),
    ally_goal(Request.ally, Character, AllyGoal),
    Minimum = Request.min_rating,
    Goal = (character(Character), SideGoal, RoleGoal,
            threat_rating(Character, Rating), Rating >= Minimum, AllyGoal).

side_goal([any], _, true) :- !.
side_goal([Side], C, Goal) :- !, one_side_goal(Side, C, Goal).
side_goal([Side|Rest], C, (First;Others)) :-
    one_side_goal(Side, C, First), side_goal(Rest, C, Others).
one_side_goal(imperium, C, imperial_character(C)).
one_side_goal(chaos, C, chaos_character(C)).
one_side_goal(xenos, C, xenos_character(C)).

role_goal(any, _, true).
role_goal(psyker, C, psyker(C)).
role_goal(commander, C, commander(C)).
role_goal(non_psyker, C, \+ psyker(C)).
role_goal(psyker_commander, C, (psyker(C), commander(C))).
role_goal(psyker_or_commander, C, (psyker(C); commander(C))).
ally_goal(none, _, true) :- !.
ally_goal(Partner, C, allies(Partner, C)).

% Сторона выводится через правила ЛР1, не хранится повторно для персонажа.
character_side(C, imperium) :- imperial_character(C).
character_side(C, chaos) :- chaos_character(C).
character_side(C, xenos) :- xenos_character(C).

% recommend(+Request, -Results): все точные совпадения, без дубликатов.
% Ранжирование: рейтинг по убыванию; при равенстве — атом имени по возрастанию.
recommend(Request, Results) :-
    build_query(Request, C, Goal),
    findall(C, call(Goal), Raw), sort(Raw, Characters),
    maplist(ranked_card(Request), Characters, Pairs),
    keysort(Pairs, Sorted), pairs_values(Sorted, Results), !.

ranked_card(Request, C, (Negative-C)-Card) :-
    threat_rating(C, Rating), Negative is -Rating,
    belongs_to(C, Faction), character_side(C, Side),
    (psyker(C) -> Psyker = true; Psyker = false),
    (commander(C) -> Commander = true; Commander = false),
    Card = result{character:C, faction:Faction, side:Side, rating:Rating,
                  psyker:Psyker, commander:Commander, request:Request}.

% Диагностика пустого результата: условия не ослабляются автоматически.
% Возвращаются до трёх НЕ рекомендованных персонажей с минимумом нарушений.
nearest(Request, Nearest) :-
    valid_request(Request),
    findall((Count-SidePenalty-RolePenalty-Negative-C)-rejected{character:C, failures:Failures},
            (character(C), failed_conditions(Request, C, Failures),
             length(Failures, Count), Count > 0,
             (memberchk(side_mismatch(_), Failures) -> SidePenalty = 1; SidePenalty = 0),
             (memberchk(role_mismatch(_), Failures) -> RolePenalty = 1; RolePenalty = 0),
             threat_rating(C, Rating), Negative is -Rating), Pairs),
    keysort(Pairs, Sorted), pairs_values(Sorted, All),
    first_three(All, Nearest).
first_three([A,B,C|_], [A,B,C]) :- !.
first_three(Short, Short).

failed_conditions(Request, C, Failures) :-
    findall(Failure, failed_condition(Request, C, Failure), Failures).
failed_condition(R, C, side_mismatch(Side)) :-
    character_side(C, Side), side_goal(R.sides, C, Goal), \+ call(Goal).
failed_condition(R, C, role_mismatch(Role)) :-
    Role = R.role, role_goal(Role, C, Goal), \+ call(Goal).
failed_condition(R, C, rating_too_low(Rating, Minimum)) :-
    Minimum = R.min_rating, threat_rating(C, Rating), Rating < Minimum.
failed_condition(R, C, not_ally(Partner)) :-
    Partner = R.ally, Partner \== none, \+ allies(Partner, C).

failure_text(side_mismatch(Side), Text) :-
    side_name(Side, Name), format(string(Text), "другая сторона: ~s", [Name]).
failure_text(role_mismatch(Role), Text) :-
    role_name(Role, Name), format(string(Text), "не выполнено условие роли «~s»", [Name]).
failure_text(rating_too_low(Rating, Minimum), Text) :-
    format(string(Text), "учебный рейтинг ~d меньше порога ~d", [Rating, Minimum]).
failure_text(not_ally(Partner), Text) :-
    character_name(Partner, Name),
    format(string(Text), "союзник «~s» не подтверждён правилами сценария", [Name]).

% === Только подписи интерфейса; факты предметной области остаются в ЛР1 ===
character_name(roboute_guilliman, "Робаут Жиллиман").
character_name(mephiston, "Мефистон").
character_name(abaddon, "Абаддон").
character_name(ahriman, "Ариман").
character_name(eldrad_ulthran, "Эльдрад Ультран").
character_name(imotekh, "Имотех").
faction_name(ultramarines, "Ультрамарины").
faction_name(blood_angels, "Кровавые Ангелы").
faction_name(black_legion, "Чёрный Легион").
faction_name(thousand_sons, "Тысяча Сынов").
faction_name(aeldari, "Аэльдари").
faction_name(necrons, "Некроны").
side_name(imperium, "Империум").
side_name(chaos, "Хаос").
side_name(xenos, "ксеносы").
side_name(any, "любая").
role_name(any, "любая").
role_name(psyker, "псайкер").
role_name(commander, "военачальник").
role_name(non_psyker, "не псайкер").
role_name(psyker_commander, "псайкер и военачальник").
role_name(psyker_or_commander, "псайкер или военачальник").

% === Диалог, объяснения и демонстрационные сценарии ===
run_dialog(Input, Output) :- run_dialog(Input, Output, []).
run_dialog(Input, Output, Options) :-
    catch(dialog(Input, Output, Options),
          dialog_stopped(Reason), stopped_message(Reason, Output)).

dialog(Input, Output, Options) :-
    format(Output, 'Warhammer 40K — подбор персонажа~n', []),
    format(Output, 'Рейтинг и союзы относятся к учебной модели. Для выхода: выход / exit.~n', []),
    format(Output, 'Формат: Сторона: Хаос; роль: псайкер~n', []),
    format(Output, 'Стороны: Империум, Хаос, ксеносы, любая. Несколько — через запятую.~n', []),
    format(Output, 'Роли: псайкер; военачальник; не псайкер; любая;~n', []),
    format(Output, '      псайкер и военачальник; псайкер или военачальник.~n~n', []),
    ask(Input, Output, 'Ваши предпочтения: ', parse_profile, Profile),
    ask(Input, Output, 'Минимальный учебный рейтинг 0..10 [Enter = 0]: ',
        parse_rating, Minimum),
    format(Output, 'Возможные союзники: Жиллиман, Мефистон, Абаддон, Ариман, Эльдрад Ультран, Имотех.~n', []),
    ask(Input, Output, 'С кем нужен союз? [Enter = без ограничения]: ',
        parse_ally, Ally),
    make_request(Profile, Minimum, Ally, Request),
    (memberchk(show_query(true), Options) -> show_query(Output, Request); true),
    recommend(Request, Results),
    print_results(Output, Request, Results).

ask(Input, Output, Prompt, Parser, Value) :-
    format(Output, '~s', [Prompt]), flush_output(Output),
    read_line_to_string(Input, Line),
    (Line == end_of_file -> throw(dialog_stopped(eof)); true),
    (stream_property(Input, tty(true)) -> true; format(Output, '~s~n', [Line])),
    normalize_space(string(Trimmed), Line), string_lower(Trimmed, Lower),
    (memberchk(Lower, ["выход", "exit", "quit"])
    -> throw(dialog_stopped(cancelled)); true),
    catch((call(Parser, Line, Value), Status = ok),
          error(input_error(Message), _), Status = invalid(Message)),
    ( Status == ok -> true
    ; Status = invalid(Text),
      format(Output, '~nОшибка: ~s~n', [Text]),
      ask(Input, Output, Prompt, Parser, Value)
    ).

stopped_message(eof, Output) :-
    format(Output, '~nВвод завершён. Подбор не выполнен: диалог не закончен.~n', []).
stopped_message(cancelled, Output) :-
    format(Output, '~nПодбор отменён пользователем.~n', []).

show_query(Output, Request) :-
    build_query(Request, _, Goal), copy_term(Goal, Printed), numbervars(Printed, 0, _),
    format(Output, '~nЛогический запрос (A — персонаж, B — рейтинг):~n', []),
    write_term(Output, Printed, [quoted(true), numbervars(true)]),
    format(Output, '.~n', []).

print_results(Output, Request, []) :- !,
    format(Output, '~nТочных совпадений нет. Условия не ослаблены.~n', []),
    nearest(Request, Rejected),
    format(Output, 'Ближайшие варианты НЕ рекомендованы по следующим причинам:~n', []),
    forall(member(Item, Rejected), print_rejected(Output, Item)).
print_results(Output, _, Results) :-
    length(Results, Count),
    format(Output, '~nНайдено: ~d. Порядок: рейтинг по убыванию, затем имя.~n', [Count]),
    print_cards(Results, 1, Output).

print_cards([], _, _).
print_cards([Card|Rest], Index, Output) :-
    character_name(Card.character, Name), faction_name(Card.faction, Faction),
    side_name(Card.side, Side), role_name(Card.request.role, Role),
    format(Output, '~n~d. ~s (~w)~n', [Index, Name, Card.character]),
    format(Output, '   Фракция: ~s. Сторона: ~s. Учебный рейтинг: ~d/10.~n',
           [Faction, Side, Card.rating]),
    format(Output, '   Почему: сторона соответствует запросу; условие роли «~s» выполнено; ~d >= ~d.~n',
           [Role, Card.rating, Card.request.min_rating]),
    trait_names(Card, Traits), format(Output, '   Признаки в базе: ~s.~n', [Traits]),
    (Card.request.ally == none -> true
    ; character_name(Card.request.ally, Partner),
      format(Output, '   Союзник «~s» подтверждён правилами учебного сценария.~n', [Partner])),
    Next is Index + 1, print_cards(Rest, Next, Output).

trait_names(Card, Text) :-
    (Card.psyker == true -> Psyker = "псайкер"; Psyker = "не псайкер"),
    (Card.commander == true
    -> format(string(Text), '~s, военачальник', [Psyker])
    ; format(string(Text), '~s; признак военачальника не отмечен', [Psyker])).

print_rejected(Output, Item) :-
    character_name(Item.character, Name),
    maplist(failure_text, Item.failures, Texts), atomics_to_string(Texts, "; ", Reason),
    format(Output, '  - ~s: ~s.~n', [Name, Reason]).

print_help(Output) :-
    format(Output, 'ЛР2 Warhammer 40K. Запуск: swipl -q -s main.pl~n', []),
    format(Output, 'Флаги после --: --show-query | --demo | --help~n', []),
    format(Output, 'Пример: swipl -q -s main.pl -- --show-query~n', []),
    format(Output, 'Сначала строка «Сторона: Хаос; роль: псайкер», затем рейтинг и союзник.~n', []).

% Шесть воспроизводимых сценариев: строки проходят тот же диалог и парсер.
run_demo :-
    forall(demo_case(Number, Lines),
           (format('~n========== Сценарий ~d ==========~n', [Number]),
            format('Входные строки: ~q~n', [Lines]),
            atomics_to_string(Lines, "\n", Text0), string_concat(Text0, "\n", Text),
            setup_call_cleanup(open_string(Text, Input),
                               run_dialog(Input, current_output, [show_query(true)]),
                               close(Input)))).
demo_case(1, ["Сторона: Хаос; роль: псайкер", "8", ""]).
demo_case(2, ["Сторона: ксеносы; роль: не псайкер", "8", ""]).
demo_case(3, ["Сторона: Империум; роль: любая", "", ""]).
demo_case(4, ["Сторона: Хаос; роль: военачальник", "8", "Ариман"]).
demo_case(5, ["Сторона: Хаос, ксеносы; роль: не псайкер", "9", ""]).
demo_case(6, ["Сторона: Империум; роль: псайкер", "8", ""]).
