USE EloRanker;

DROP TABLE IF EXISTS EloRanker.User;
CREATE TABLE IF NOT EXISTS EloRanker.User (
	UserId INT NOT NULL AUTO_INCREMENT,
	FirstName VARCHAR(127),
	LastName VARCHAR(127),
	Email VARCHAR(127),
	PRIMARY KEY (UserId)
);

DROP TABLE IF EXISTS EloRanker.League;
CREATE TABLE IF NOT EXISTS EloRanker.League (
	LeagueId INT NOT NULL AUTO_INCREMENT,
	Name VARCHAR(127),
	Description VARCHAR(511),
	PRIMARY KEY (LeagueId)
);

DROP TABLE IF EXISTS EloRanker.LeagueMetadata;
CREATE TABLE IF NOT EXISTS EloRanker.LeagueMetadata (
	LeagueId INT NOT NULL AUTO_INCREMENT,
	InitialRating INT NOT NULL,
	KFactor INT NOT NULL,
	ProvisionalKFactor INT NOT NULL,
	NumProvisionalGames INT NOT NULL,
	PRIMARY KEY (LeagueId)
);

DROP TABLE IF EXISTS EloRanker.LeaguePlayer;
CREATE TABLE IF NOT EXISTS EloRanker.LeaguePlayer (
	LeaguePlayerId INT NOT NULL AUTO_INCREMENT,
	LeagueId INT NOT NULL,
	UserId INT,
	LeaguePlayerName VARCHAR(63) NOT NULL,
	RatingUpdated DATETIME NOT NULL,
	PRIMARY KEY (LeaguePlayerId),
	INDEX `LeagueIdLeaguePlayerName` (LeaguePlayerId, LeaguePlayerName),
	CONSTRAINT leagueId_playerName UNIQUE (LeagueId, LeaguePlayerName)
);

DROP TABLE IF EXISTS EloRanker.Rating;
CREATE TABLE IF NOT EXISTS EloRanker.Rating (
	LeaguePlayerId INT NOT NULL AUTO_INCREMENT,
	GameDate DATETIME NOT NULL,
	Rating INT NOT NULL,
	GamesPlayed INT NOT NULL DEFAULT 0,
	PRIMARY KEY (LeaguePlayerId, GameDate)
);

DROP TABLE IF EXISTS EloRanker.LeagueAdmin;
CREATE TABLE IF NOT EXISTS EloRanker.LeagueAdmin (
	LeagueId INT NOT NULL,
	UserId INT NOT NULL,
	PRIMARY KEY (LeagueId, UserId)
);

DROP TABLE IF EXISTS EloRanker.GameResult;
CREATE TABLE IF NOT EXISTS EloRanker.GameResult (
	GameResultId INT NOT NULL AUTO_INCREMENT,
    LeagueId INT NOT NULL,
    FirstLeaguePlayerId INT NOT NULL,
    SecondLeaguePlayerId INT NOT NULL,
    Result INT NOT NULL,
    GameDate DATETIME NOT NULL,
	PRIMARY KEY (GameResultId),
    INDEX `LeagueIdGameDate` (LeagueId, GameDate)
);

DROP TABLE IF EXISTS EloRanker.Tournament;
CREATE TABLE IF NOT EXISTS EloRanker.Tournament (
	TournamentId INT NOT NULL AUTO_INCREMENT,
	Abbreviation VARCHAR(15),
	Name VARCHAR(127),
	LeagueId INT NOT NULL,
	Description VARCHAR(511),
	PRIMARY KEY (TournamentId),
	UNIQUE INDEX (LeagueId, Name),
	UNIQUE INDEX (LeagueId, Abbreviation)
);

DROP TABLE IF EXISTS EloRanker.TournamentMetadata;
CREATE TABLE IF NOT EXISTS EloRanker.TournamentMetadata (
	TournamentId INT NOT NULL AUTO_INCREMENT,
	WinPoints FLOAT,
	DrawPoints FLOAT,
	LosePoints FLOAT,
	PRIMARY KEY (TournamentId)
);

DROP TABLE IF EXISTS EloRanker.TournamentGameResult;
CREATE TABLE IF NOT EXISTS EloRanker.TournamentGameResult (
	TournamentId INT NOT NULL,	
	GameResultId INT NOT NULL,
	PRIMARY KEY (TournamentId, GameResultId)
);