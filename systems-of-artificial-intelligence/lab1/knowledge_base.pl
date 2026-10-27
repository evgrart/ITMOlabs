:- encoding(utf8).
:- module(knowledge_base, [
    faction/1, character/1, psyker/1, commander/1,
    imperial_faction/1, chaos_faction/1, xenos_faction/1,
    belongs_to/2, threat_rating/2, hostile_faction/2,
    imperial_character/1, chaos_character/1, xenos_character/1,
    dangerous/1, non_psyker/1, enemies/2, allies/2
]).

% ЛР1. Warhammer 40,000: персонажи, фракции и отношения между ними.
% База: 25 одноаргументных фактов, 15 двухаргументных, 7 правил.
% Это срез вселенной для изучения логического вывода, не правила настолки.
% threat_rating — условный учебный рейтинг 0..10, не официальный параметр.
% commander — выбранные военачальники модели, не полный список лидеров лора.
% hostile_faction — три заданных конфликта, не вся дипломатия вселенной.

% === Одноаргументные факты (25) ===
% faction(Faction): фракция, представленная в учебной базе (6).
faction(ultramarines).
faction(blood_angels).
faction(black_legion).
faction(thousand_sons).
faction(aeldari).
faction(necrons).

% character(Character): персонаж (6).
character(roboute_guilliman).
character(mephiston).
character(abaddon).
character(ahriman).
character(eldrad_ulthran).
character(imotekh).

% psyker(Character): известные псайкеры (3).
% Для этих шести персонажей набор считается полным; см. non_psyker/1.
psyker(mephiston).
psyker(ahriman).
psyker(eldrad_ulthran).

% commander(Character): выбранные военачальники (4).
% Psyker и commander совместимы: Ариман имеет оба признака.
commander(roboute_guilliman).
commander(abaddon).
commander(ahriman).
commander(imotekh).

% Категории фракций (2 + 2 + 2). Xenos — категория, не общий союз.
imperial_faction(ultramarines).
imperial_faction(blood_angels).
chaos_faction(black_legion).
chaos_faction(thousand_sons).
xenos_faction(aeldari).
xenos_faction(necrons).

% === Двухаргументные факты (15) ===
% belongs_to(Character, Faction): принадлежность/основная связь с фракцией (6).
belongs_to(roboute_guilliman, ultramarines).
belongs_to(mephiston, blood_angels).
belongs_to(abaddon, black_legion).
belongs_to(ahriman, thousand_sons).
belongs_to(eldrad_ulthran, aeldari).
belongs_to(imotekh, necrons).

% threat_rating(Character, Rating): УЧЕБНАЯ оценка угрозы, целое 0..10 (6).
% Числа выбраны для проверки сравнения и границы >= 8, а не баланса игры.
threat_rating(roboute_guilliman, 10).
threat_rating(mephiston, 7).
threat_rating(abaddon, 9).
threat_rating(ahriman, 8).
threat_rating(eldrad_ulthran, 9).
threat_rating(imotekh, 8).

% hostile_faction(Faction, Other): выбранные конфликты (3).
% Записаны в одном направлении; enemies/2 проверяет оба направления.
hostile_faction(ultramarines, black_legion).
hostile_faction(blood_angels, thousand_sons).
hostile_faction(aeldari, necrons).

% === Правила (7) ===
% 1. Персонаж Империума: композиция принадлежности и категории фракции.
imperial_character(Character) :-
    character(Character), belongs_to(Character, Faction), imperial_faction(Faction).

% 2. Персонаж Хаоса.
chaos_character(Character) :-
    character(Character), belongs_to(Character, Faction), chaos_faction(Faction).

% 3. Персонаж ксеносской фракции. Не означает союз со всеми ксеносами.
xenos_character(Character) :-
    character(Character), belongs_to(Character, Faction), xenos_faction(Faction).

% 4. Учебная классификация опасных персонажей: рейтинг >= 8 И
% (псайкер ИЛИ военачальник). У Аримана два доказательства; setof их удаляет.
dangerous(Character) :-
    character(Character), threat_rating(Character, Rating), Rating >= 8,
    (psyker(Character); commander(Character)).

% 5. Известный персонаж, не являющийся псайкером в данном полном срезе.
% Сначала character/1 связывает переменную, затем выполняется отрицание.
non_psyker(Character) :-
    character(Character), \+ psyker(Character).

% 6. Враги: персонажи разных сторон одного из заданных конфликтов.
% Симметричность обеспечена дизъюнкцией направлений. Самовражда исключена.
enemies(First, Second) :-
    character(First), character(Second), dif(First, Second),
    belongs_to(First, Faction), belongs_to(Second, OtherFaction),
    (hostile_faction(Faction, OtherFaction); hostile_faction(OtherFaction, Faction)).

% 7. Союзники в УЧЕБНОМ сценарии: два разных персонажа Империума
% либо два разных персонажа Хаоса. Это допущение, не правило состава армии.
% Общая метка xenos не создаёт союз. Отношение симметрично, не рефлексивно.
allies(First, Second) :-
    character(First), character(Second), dif(First, Second),
    ( (imperial_character(First), imperial_character(Second))
    ; (chaos_character(First), chaos_character(Second))
    ).

% === Примеры запросов (вывод: swipl -q -s queries.pl -g main -t halt) ===
% ?- character(ahriman).                      % true.
% ?- character(unknown_character).            % false.
% ?- setof(C, psyker(C), L).                  % [ahriman,eldrad_ulthran,mephiston]
% ?- setof(C, (psyker(C), commander(C)), L).   % [ahriman]
% ?- setof(C, (psyker(C); commander(C)), L).   % Все 6 персонажей без повторов.
% ?- setof(C, (character(C), \+ psyker(C)), L).
%                                             % [abaddon,imotekh,roboute_guilliman]
% ?- setof(C, imperial_character(C), L).       % [mephiston,roboute_guilliman]
% ?- setof(C, dangerous(C), L).
%                                             % [abaddon,ahriman,eldrad_ulthran,imotekh,roboute_guilliman]
% ?- setof(C, enemies(abaddon, C), L).        % [roboute_guilliman]
% ?- setof(C, allies(ahriman, C), L).         % [abaddon]
