:- encoding(utf8).
:- module(knowledge_base, [
    game/1, rpg/1, strategy/1, puzzle/1, indie/1, cooperative/1,
    singleplayer/1, player/1, genre/1, minimum_age/2, age/2,
    likes/2, owns/2, friend_of/2, adult/1, eligible/2,
    matches_genre/2, recommended/2, social_game/1, friends/2,
    can_play_together/3
]).

% ЛР1. Учебный (вымышленный) каталог видеоигр и профили игроков.
% Факты: 25 с одним аргументом и 15 с двумя. Правила: 7.
% Признаки cooperative и singleplayer совместимы: игра может иметь оба режима.

% === Факты с одним аргументом (25) ===
% game(Game): игра входит в каталог.
game(rune_quest).
game(shadow_realm).
game(star_fleet).
game(empire_builder).
game(logic_garden).

% Жанровые признаки игр; в учебном каталоге жанры не пересекаются.
rpg(rune_quest).
rpg(shadow_realm).
strategy(star_fleet).
strategy(empire_builder).
puzzle(logic_garden).

% indie(Game): независимая разработка; режимы игры — отдельные признаки.
indie(rune_quest).
indie(star_fleet).
indie(logic_garden).
cooperative(rune_quest).
cooperative(star_fleet).
cooperative(empire_builder).
singleplayer(shadow_realm).
singleplayer(empire_builder).
singleplayer(logic_garden).

% player(Player): зарегистрированный игрок; genre(Genre): допустимый жанр.
player(anna).
player(boris).
player(vera).
genre(rpg).
genre(strategy).
genre(puzzle).

% === Атрибуты и отношения с двумя аргументами (15) ===
% minimum_age(Game, Age): учебное возрастное ограничение, целое число >= 0.
minimum_age(rune_quest, 12).
minimum_age(shadow_realm, 18).
minimum_age(star_fleet, 6).
minimum_age(empire_builder, 12).
minimum_age(logic_garden, 0).

% age(Player, Age): возраст игрока в полных годах.
age(anna, 20).
age(boris, 12).
age(vera, 6).

% likes(Player, Genre): жанровое предпочтение игрока.
likes(anna, rpg).
likes(boris, strategy).
likes(vera, puzzle).

% owns(Player, Game): полная библиотека каждого из трёх учебных профилей.
% Отсутствие owns для известной пары здесь означает, что игра не куплена.
owns(anna, rune_quest).
owns(boris, empire_builder).

% friend_of(Player, Other): дружба записана в одном направлении;
% правило friends/2 делает отношение симметричным, исключая дружбу с собой.
friend_of(anna, boris).
friend_of(boris, vera).

% === Правила (7) ===
% 1. adult(Player): совершеннолетие по возрасту, а не отдельный факт.
adult(Player) :-
    player(Player), age(Player, Age), Age >= 18.

% 2. eligible(Player, Game): игрок проходит возрастное ограничение.
eligible(Player, Game) :-
    player(Player), game(Game),
    age(Player, Age), minimum_age(Game, Minimum), Age >= Minimum.

% 3. matches_genre(Player, Game): конъюнкция и дизъюнкция связывают
% предпочтение-жанр с одноаргументными жанровыми признаками игр.
matches_genre(Player, Game) :-
    player(Player), game(Game), likes(Player, Genre),
    ( (Genre = rpg, rpg(Game))
    ; (Genre = strategy, strategy(Game))
    ; (Genre = puzzle, puzzle(Game))
    ).

% 4. recommended(Player, Game): композиция правил + отрицание как неудача.
% К моменту \+ owns/2 обе переменные связаны предыдущими целями.
recommended(Player, Game) :-
    eligible(Player, Game), matches_genre(Player, Game),
    \+ owns(Player, Game).

% 5. social_game(Game): игра подходит для социального/стратегического досуга.
% Если оба признака истинны, возможны повторные решения; setof/3 их удаляет.
social_game(Game) :-
    game(Game), (cooperative(Game); strategy(Game)).

% 6. friends(Player, Other): симметричная дружба различных игроков.
friends(Player, Other) :-
    player(Player), player(Other), dif(Player, Other),
    (friend_of(Player, Other); friend_of(Other, Player)).

% 7. can_play_together(P, Q, Game): друзья могут играть совместно,
% если обоим разрешена кооперативная игра. Владение здесь не требуется:
% правило проверяет возраст и режим, а не лицензию на запуск.
can_play_together(Player, Other, Game) :-
    friends(Player, Other), cooperative(Game),
    eligible(Player, Game), eligible(Other, Game).

% === Тест-запросы возрастающей сложности ===
% ?- game(rune_quest).                         % true.
% ?- game(unknown_game).                       % false.
% ?- setof(G, rpg(G), Games).                  % [rune_quest,shadow_realm]
% ?- setof(G, (indie(G), cooperative(G)), L).   % [rune_quest,star_fleet]
% ?- setof(G, (rpg(G); puzzle(G)), L).         % [logic_garden,rune_quest,shadow_realm]
% ?- setof(G, (game(G), \+ cooperative(G)), L).% [logic_garden,shadow_realm]
% ?- setof(P, adult(P), L).                    % [anna]
% ?- setof(G, eligible(vera, G), L).           % [logic_garden,star_fleet]
% ?- setof(P-G, recommended(P, G), L).        % [anna-shadow_realm,boris-star_fleet,vera-logic_garden]
% ?- setof(G, can_play_together(anna, boris, G), L).
%                                             % [empire_builder,rune_quest,star_fleet]
