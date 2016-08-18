INSERT INTO EloRanker.League (LeagueId, Name, Description)
VALUES (1, 'Will\'s Test League', 'Will\'s League Used for Testing');

INSERT INTO EloRanker.LeagueMetadata(LeagueId, InitialRating, KFactor, ProvisionalKFactor, NumProvisionalGames) VALUES
(1, 1000, 24, 48, 10);

INSERT INTO EloRanker.User (UserId, FirstName, LastName, Email) VALUES
(1, 'Will', 'Lee', 'wjl57@cornell.edu');

INSERT INTO EloRanker.LeagueAdmin (LeagueId, UserId) VALUES
(1, 1);

INSERT INTO EloRanker.LeaguePlayer (LeagueId, UserId, LeaguePlayerName) VALUES
(1, 1, 'wlee'),
(2, 1, null, 'dhoppe'),
(3, 1, null, 'dgutz');

