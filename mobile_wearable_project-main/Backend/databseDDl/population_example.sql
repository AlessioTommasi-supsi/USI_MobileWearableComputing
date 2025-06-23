USE USI_MobileWearableProject;
-- Popolamento della tabella User
INSERT INTO `User` (`id`, `name`, `email`) VALUES
(1, 'Default_user', 'user not defined because do not implement user functionality'),
(2, 'Giulia Bianchi', 'giulia.bianchi@example.com'),
(3, 'Luca Verdi', 'luca.verdi@example.com');

-- Popolamento della tabella Attachment
INSERT INTO `Attachment` (`id`, `url`, `type`) VALUES
(1, 'http://example.com/image1.jpg', 'image'),
(2, 'http://example.com/image2.jpg', 'image'),
(3, 'http://example.com/image3.jpg', 'image');

-- Popolamento della tabella gps
INSERT INTO `gps` (`id`, `latitude`, `longitude`) VALUES
(1, 45.12345, 7.12345),
(2, 45.54321, 7.54321),
(3, 45.98765, 7.98765);



-- Popolamento della tabella Post
INSERT INTO `Post` (`id`, `message`, `fk_location`, `fk_attachment`, `fk_creator`, `expirationDateTime`, `visibility_radius`) VALUES
(1, 'Post di esempio 1', 1, 1, 1, '2025-12-31 23:59:59', 100),
(2, 'Post di esempio 2', 2, 2, 2, '2025-12-31 23:59:59', 200),
(3, 'Post di esempio 3', 3, 3, 3, '2025-12-31 23:59:59', 300);

-- Aggiunta di 10 post di Mario a Lugano
INSERT INTO `Post` (`id`, `message`, `fk_location`, `fk_attachment`, `fk_creator`, `expirationDateTime`, `visibility_radius`) VALUES
(4, 'Post di Mario a Lugano 1', 1, 1, 1, '2025-12-31 23:59:59', 100),
(5, 'Post di Mario a Lugano 2', 1, 1, 1, '2025-12-31 23:59:59', 100),
(6, 'Post di Mario a Lugano 3', 1, 1, 1, '2025-12-31 23:59:59', 100),
(7, 'Post di Mario a Lugano 4', 1, 1, 1, '2025-12-31 23:59:59', 100),
(8, 'Post di Mario a Lugano 5', 1, 1, 1, '2025-12-31 23:59:59', 100),
(9, 'Post di Mario a Lugano 6', 1, 1, 1, '2025-12-31 23:59:59', 100),
(10, 'Post di Mario a Lugano 7', 1, 1, 1, '2025-12-31 23:59:59', 100),
(11, 'Post di Mario a Lugano 8', 1, 1, 1, '2025-12-31 23:59:59', 100),
(12, 'Post di Mario a Lugano 9', 1, 1, 1, '2025-12-31 23:59:59', 100),
(13, 'Post di Mario a Lugano 10', 1, 1, 1, '2025-12-31 23:59:59', 100);


-- Popolamento della tabella Share
INSERT INTO `Share` (`id`, `fk_post`, `fk_user`) VALUES
(1, 1, 2),  -- Giulia condivide il Post 1
(2, 1, 3),  -- Luca condivide il Post 1
(3, 2, 1),  -- Mario condivide il Post 2
(4, 3, 1),  -- Mario condivide il Post 3
(5, 3, 2);  -- Giulia condivide il Post 3

