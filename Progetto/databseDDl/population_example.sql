-- Popolamento della tabella User
INSERT INTO `User` (`id`, `name`, `email`) VALUES
(1, 'Mario Rossi', 'mario.rossi@example.com'),
(2, 'Giulia Bianchi', 'giulia.bianchi@example.com'),
(3, 'Luca Verdi', 'luca.verdi@example.com');

-- Popolamento della tabella Attachment
INSERT INTO `Attachment` (`id`, `url`) VALUES
(1, 'http://example.com/image1.jpg'),
(2, 'http://example.com/image2.jpg'),
(3, 'http://example.com/image3.jpg');

-- Popolamento della tabella Post
INSERT INTO `Post` (`id`, `message`, `GPS_location`, `fk_attachment`, `fk_creator`) VALUES
(1, 'Post di esempio 1', '45.12345, 7.12345', 1, 1),
(2, 'Post di esempio 2', '45.54321, 7.54321', 2, 2),
(3, 'Post di esempio 3', '45.98765, 7.98765', 3, 3);

-- Popolamento della tabella Share
INSERT INTO `Share` (`id`, `fk_post`, `fk_user`) VALUES
(1, 1, 2),  -- Giulia condivide il Post 1
(2, 1, 3),  -- Luca condivide il Post 1
(3, 2, 1),  -- Mario condivide il Post 2
(4, 3, 1),  -- Mario condivide il Post 3
(5, 3, 2);  -- Giulia condivide il Post 3
