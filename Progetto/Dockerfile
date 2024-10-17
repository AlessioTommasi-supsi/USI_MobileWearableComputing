# Usa l'immagine PHP con Apache come base
FROM php:apache

# Installa le estensioni richieste
RUN docker-php-ext-install pdo pdo_mysql

# Assicura che il server Apache possa scrivere nella directory
RUN chown -R www-data:www-data /var/www/html