SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `Post`;
DROP TABLE IF EXISTS `Attachment`;
DROP TABLE IF EXISTS `User`;
DROP TABLE IF EXISTS `Share`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `Post` (
    `id` INTEGER NOT NULL,
    `message` VARCHAR(255) NOT NULL,
    `GPS_location` VARCHAR(255) NOT NULL,
    `fk_attachment` INTEGER NOT NULL,
    `fk_creator` INTEGER NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`id`)
);

CREATE TABLE `Attachment` (
    `id` INTEGER NOT NULL,
    `url` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `User` (
    `id` INTEGER NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `Share` (
    `id` INTEGER NOT NULL,
    `fk_post` INTEGER NOT NULL,
    `fk_user` INTEGER NOT NULL,
    PRIMARY KEY (`id`)
);

ALTER TABLE `Post` ADD FOREIGN KEY (`fk_creator`) REFERENCES `User`(`id`);
ALTER TABLE `Post` ADD FOREIGN KEY (`fk_attachment`) REFERENCES `Attachment`(`id`);
ALTER TABLE `Share` ADD FOREIGN KEY (`fk_post`) REFERENCES `Post`(`id`);
ALTER TABLE `Share` ADD FOREIGN KEY (`fk_user`) REFERENCES `User`(`id`);