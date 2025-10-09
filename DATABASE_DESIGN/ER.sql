CREATE TABLE "User" (
  "UserId" int PRIMARY KEY,
  "FirstName" varchar,
  "LastName" varchar,
  "ContactEmail" varchar,
  "Gender" varchar,
  "BirthDate" date,
  "PhoneCountryCode" varchar,
  "PhoneNumber" varchar,
  "LanguageId" int,
  "NationalityId" int,
  "SpokenLanguages" json,
  "RegistrationStep" int,
  "CityId" int,
  "CreatedAt" datetime,
  "UpdatedAt" datetime
);

CREATE TABLE "Otp" (
  "OtpId" uuid PRIMARY KEY,
  "OtpCode" varchar,
  "LastTimeChanged" datetime,
  "OtpCount" int,
  "OtpType" varchar,
  "UserId" int
);

CREATE TABLE "Language" (
  "LanguageId" int PRIMARY KEY,
  "LanguageName" varchar,
  "LanguageCode" varchar,
  "IsActive" boolean
);

CREATE TABLE "UserAuthMethod" (
  "UserAuthMethodId" int PRIMARY KEY,
  "UserId" int,
  "AuthProvider" varchar,
  "AuthenticationEmail" varchar,
  "PasswordHash" varchar,
  "CreatedAt" datetime,
  "UpdatedAt" datetime
);

CREATE TABLE "Country" (
  "CountryId" int PRIMARY KEY,
  "CountryName" varchar,
  "PhoneCallingCode" varchar,
  "FlagUrl" varchar
);

CREATE TABLE "City" (
  "CityId" int PRIMARY KEY,
  "CityName" varchar,
  "CountryId" int
);

CREATE TABLE "Club" (
  "ClubId" int PRIMARY KEY,
  "ClubName" varchar,
  "LogoUrl" varchar,
  "CityId" int
);

CREATE TABLE "Federation" (
  "FederationId" int PRIMARY KEY,
  "FederationName" varchar,
  "LogoUrl" varchar,
  "CountryId" int
);

CREATE TABLE "Sport" (
  "SportId" int PRIMARY KEY,
  "SportName" varchar
);

CREATE TABLE "Position" (
  "PositionId" int PRIMARY KEY,
  "Abbreviation" varchar,
  "PositionLabel" varchar
);

CREATE TABLE "PlayerCategory" (
  "PlayerCategoryId" int PRIMARY KEY,
  "CategoryLabel" varchar,
  "CategoryGender" varchar
);

CREATE TABLE "Player" (
  "PlayerId" int PRIMARY KEY,
  "ProfilePhoto" varchar,
  "Height" int,
  "Weight" int,
  "Foot" varchar,
  "PositionId" int,
  "PlayerCategoryId" int,
  "CreatedAt" datetime,
  "UpdatedAt" datetime
);

CREATE TABLE "PlayerSport" (
  "PlayerSportId" int PRIMARY KEY,
  "UserId" int,
  "PlayerId" int,
  "SportId" int
);

CREATE TABLE "Education" (
  "EducationId" int PRIMARY KEY,
  "PlayerId" int,
  "CityId" int,
  "School" varchar,
  "StartDate" date,
  "EndDate" date,
  "Degree" varchar
);

CREATE TABLE "SportsExperience" (
  "SportsExperienceId" int PRIMARY KEY,
  "PlayerId" int,
  "SportId" int,
  "StartDate" date,
  "EndDate" date,
  "ContractType" varchar,
  "League" varchar,
  "StrongFoot" varchar,
  "PrimaryPositionId" int,
  "SecondaryPositionId" int,
  "PlayerCategoryId" int,
  "ClubId" int
);

ALTER TABLE "User" ADD FOREIGN KEY ("LanguageId") REFERENCES "Language" ("LanguageId");

ALTER TABLE "User" ADD FOREIGN KEY ("CityId") REFERENCES "City" ("CityId");

ALTER TABLE "User" ADD FOREIGN KEY ("NationalityId") REFERENCES "Country" ("CountryId");

ALTER TABLE "Otp" ADD FOREIGN KEY ("UserId") REFERENCES "User" ("UserId");

ALTER TABLE "UserAuthMethod" ADD FOREIGN KEY ("UserId") REFERENCES "User" ("UserId");

ALTER TABLE "PlayerSport" ADD FOREIGN KEY ("UserId") REFERENCES "User" ("UserId");

ALTER TABLE "PlayerSport" ADD FOREIGN KEY ("PlayerId") REFERENCES "Player" ("PlayerId");

ALTER TABLE "PlayerSport" ADD FOREIGN KEY ("SportId") REFERENCES "Sport" ("SportId");

ALTER TABLE "Player" ADD FOREIGN KEY ("PositionId") REFERENCES "Position" ("PositionId");

ALTER TABLE "Player" ADD FOREIGN KEY ("PlayerCategoryId") REFERENCES "PlayerCategory" ("PlayerCategoryId");

ALTER TABLE "Education" ADD FOREIGN KEY ("PlayerId") REFERENCES "Player" ("PlayerId");

ALTER TABLE "Education" ADD FOREIGN KEY ("CityId") REFERENCES "City" ("CityId");

ALTER TABLE "SportsExperience" ADD FOREIGN KEY ("PlayerCategoryId") REFERENCES "PlayerCategory" ("PlayerCategoryId");

ALTER TABLE "SportsExperience" ADD FOREIGN KEY ("ClubId") REFERENCES "Club" ("ClubId");

ALTER TABLE "SportsExperience" ADD FOREIGN KEY ("PrimaryPositionId") REFERENCES "Position" ("PositionId");

ALTER TABLE "SportsExperience" ADD FOREIGN KEY ("SecondaryPositionId") REFERENCES "Position" ("PositionId");

ALTER TABLE "SportsExperience" ADD FOREIGN KEY ("PlayerId") REFERENCES "Player" ("PlayerId");

ALTER TABLE "SportsExperience" ADD FOREIGN KEY ("SportId") REFERENCES "Sport" ("SportId");

ALTER TABLE "Country" ADD FOREIGN KEY ("CountryId") REFERENCES "City" ("CountryId");

ALTER TABLE "Club" ADD FOREIGN KEY ("CityId") REFERENCES "City" ("CityId");

ALTER TABLE "Country" ADD FOREIGN KEY ("CountryId") REFERENCES "Federation" ("CountryId");
