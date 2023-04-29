FROM php:7.4-fpm

# Install dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    libpng-dev \
    libonig-dev \
    libxml2-dev \
    zip \
    unzip \
    libzip-dev \
    && pecl install redis \
    && docker-php-ext-enable redis

# Install extensions
RUN docker-php-ext-install pdo_mysql mbstring exif pcntl bcmath gd zip soap

# Install PHPUnit
RUN curl -sSLo /tmp/phpunit.phar https://phar.phpunit.de/phpunit.phar \
    && chmod +x /tmp/phpunit.phar \
    && mv /tmp/phpunit.phar /usr/local/bin/phpunit

# Set working directory
#WORKDIR /home/sedki/Documents/nxtya/
WORKDIR /var/www/html
# Copy existing application directory contents
COPY . /var/www/html

# Install Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer
RUN composer install  
# Install dependencies using Composer
#COPY --from=composer:1.10.1 . /home/sedki/Documents/nxtya/docker/docker-composer.yml
COPY composer.json  /var/www/html/laravel/framework-10.x/composer.json
EXPOSE 9000
CMD ["php-fpm"]
