SELECT * FROM EloRanker.User;
SELECT * FROM EloRanker.League;
SELECT * FROM EloRanker.LeagueMetadata;
SELECT * FROM EloRanker.LeaguePlayer;
SELECT * FROM EloRanker.Rating;
SELECT * FROM EloRanker.LeagueAdmin;
SELECT * FROM EloRanker.GameResult;

DESCRIBE EloRanker.LeaguePlayer;

SELECT LeaguePlayerId FROM EloRanker.LeaguePlayer WHERE LeagueId = 1 AND LeaguePlayerName = 1;

TRUNCATE EloRanker.LeaguePlayer;
TRUNCATE EloRanker.Rating;
SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName, Rating, GamesPlayed 
	FROM EloRanker.LeaguePlayer WHERE LeagueId = 1 AND LeaguePlayerId = 1;