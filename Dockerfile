FROM php:8.3-fpm-trixie

SHELL [ "/bin/bash", "-exo", "pipefail", "-c" ]

# ATTENTION: versions must be managed in versions.props!
ARG GRAV_VERSION
ARG MVIS_VERSION

ENV GRAV_VERSION=${GRAV_VERSION}
ENV MVIS_VERSION=${MVIS_VERSION}

# renovate: datasource=github-tags depName=krakjoe/apcu versioning=semver
ENV PHP_APCU_VERSION=v5.1.28
# renovate: datasource=github-tags depName=php/pecl-file_formats-yaml versioning=semver
ENV PHP_YAML_VERSION=2.3.0


ENV YQ_VERSION=v4.52.4

ENV ADMIN_EMAIL=portal@test.de
ENV ADMIN_FULL_NAME="The Admin"
ENV TZ='Europe/Berlin'

COPY --from=ghcr.io/php/pie:bin /pie /usr/bin/pie

RUN groupadd --system foo; \
    useradd --no-log-init --system --gid foo --create-home foo; \
    \
    apt update; \
    apt install -qq -y --no-install-recommends -o Dpkg::Progress-Fancy="0" -o Dpkg::Use-Pty="0" \
        git \
        unzip \
        yq \
        rsync \
        gosu \
        cron \
        apache2-utils \
        wget \
        logrotate

RUN apt install -qq -y --no-install-recommends -o Dpkg::Progress-Fancy="0" -o Dpkg::Use-Pty="0" \
    libfreetype6-dev \
    libzip-dev \
    libpng-dev \
    libwebp-dev \
    libjpeg-dev \
    libyaml-dev \
    libxslt-dev \
    libicu-dev

RUN docker-php-ext-configure gd --with-freetype --with-jpeg --with-webp; \
	docker-php-ext-install -j "$(nproc)" \
        zip \
        gd \
        opcache \
        xsl \
        xml \
        dom \
        intl \
    ; \
    pie install apcu/apcu:${PHP_APCU_VERSION:1}; \
    pie install pecl/yaml:$PHP_YAML_VERSION; \
    \
    docker-php-ext-enable \
        apcu \
        yaml \
    ; \
#    apt purge -y --auto-remove \
#        libwebp-dev \
#        libjpeg-dev \
#        libpng-dev \
#        libfreetype6-dev \
#        libyaml-dev \
#        libzip-dev \
#    ; \
    rm -rf /var/lib/apt/lists/*; \
    \
    mv "$PHP_INI_DIR/php.ini-production" "$PHP_INI_DIR/php.ini";

WORKDIR /var/www
RUN curl -o grav-admin.zip -SL https://github.com/getgrav/grav/releases/download/${GRAV_VERSION}/grav-admin-v${GRAV_VERSION}.zip && \
    unzip -qq grav-admin.zip -d /usr/share -x "grav-admin/user/themes/*" -x "grav-admin/user/pages/*" && \
    rm grav-admin.zip

# COPY OUR ADDITIONAL THEMES AND PLUGINS
COPY user/themes /usr/share/grav-admin/user/themes
COPY user/plugins /usr/share/grav-admin/user/plugins
COPY user/pages /usr/share/grav-admin/user/pages
#COPY data /usr/share/grav-admin/user/data
COPY user/accounts /usr/share/grav-admin/user/accounts
COPY user/blueprints /usr/share/grav-admin/user/blueprints

# Install Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer
RUN cd /usr/share/grav-admin/user/plugins/ingrid-grav && composer update
RUN cd /usr/share/grav-admin/user/plugins/ingrid-grav-utils && composer update

# Install yq
RUN wget -qO /usr/local/bin/yq https://github.com/mikefarah/yq/releases/download/${YQ_VERSION}/yq_linux_amd64 \
 && chmod +x /usr/local/bin/yq

# Install mvis
RUN cd /usr/share/grav-admin \
 && curl -o mvis.zip -SL https://nexus.informationgrid.eu/repository/maven-public/de/ingrid/measurement-client/${MVIS_VERSION}/measurement-client-${MVIS_VERSION}.zip \
 && unzip mvis.zip \
 && mv /usr/share/grav-admin/measurement-client-"${MVIS_VERSION}" /usr/share/grav-admin/mvis \
 && rm mvis.zip

# Logrotate
COPY logrotate/grav /etc/logrotate.d/grav
RUN chmod 0644 /etc/logrotate.d/grav
RUN echo '*/5 * * * * root /usr/sbin/logrotate /etc/logrotate.conf' \
    > /etc/cron.d/logrotate \
    && chmod 0644 /etc/cron.d/logrotate

COPY entrypoint.sh /entrypoint.sh
#COPY grav.ini $PHP_INI_DIR/conf.d/

EXPOSE 9000

ENTRYPOINT ["/entrypoint.sh"]
CMD ["php-fpm"]
