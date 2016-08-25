SELECT * FROM EloRanker.User;
SELECT * FROM EloRanker.League;
SELECT * FROM EloRanker.LeagueMetadata;
SELECT * FROM EloRanker.LeaguePlayer;	
SELECT * FROM EloRanker.Rating;
SELECT * FROM EloRanker.LeagueAdmin;
SELECT * FROM EloRanker.GameResult;

SELECT UTC_TIMESTAMP();
SELECT NOW();

INSERT INTO EloRanker.Rating (LeaguePlayerId, GameDate, Rating, GamesPlayed) VALUES (10, CURRENT_TIMESTAMP, 9000, 0);
DESCRIBE EloRanker.LeaguePlayer;

SELECT LeaguePlayerId FROM EloRanker.LeaguePlayer WHERE LeagueId = 1 AND LeaguePlayerName = 1;

TRUNCATE EloRanker.LeaguePlayer;
TRUNCATE EloRanker.Rating;
SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName, Rating, GamesPlayed 
	FROM EloRanker.LeaguePlayer WHERE LeagueId = 1 AND LeaguePlayerId = 1;

SELECT * FROM EloRanker.LeaguePlayer;

SELECT r.LeaguePlayerId, r.GameDate, r.Rating, r.GamesPlayed 
FROM EloRanker.Rating r
JOIN EloRanker.LeaguePlayer lp
ON r.LeaguePlayerId = lp.LeaguePlayerId
WHERE r.GameDate = (SELECT MAX(r2.GameDate) 
FROM EloRanker.Rating r2
JOIN EloRanker.LeaguePlayer lp2
ON r2.LeaguePlayerId = lp2.LeaguePlayerId
WHERE lp2.LeagueId = 1
AND lp2.LeaguePlayerId = lp.LeaguePlayerId
AND r2.GameDate <= '2016-08-18 11:00:00');

SELECT r.LeaguePlayerId, r.GameDate, r.Rating, r.GamesPlayed 
FROM EloRanker.Rating r
JOIN EloRanker.LeaguePlayer lp
ON r.LeaguePlayerId = lp.LeaguePlayerId
WHERE r.GameDate = (SELECT MAX(r2.GameDate) 
FROM EloRanker.Rating r2
JOIN EloRanker.LeaguePlayer lp2
ON r2.LeaguePlayerId = lp2.LeaguePlayerId
WHERE lp2.LeagueId = 1
AND lp2.LeaguePlayerId = lp.LeaguePlayerId
AND r2.GameDate <= '2016-08-18 11:00:00');

SELECT * FROM EloRanker.GameResult WHERE GameDate >= '2016-08-18 11:00:00';

SELECT @@global.time_zone;

SET GLOBAL time_zone = '+0:00';

SET @@session.time_zone='+00:00';
SELECT @@global.time_zone, @@session.time_zone;

