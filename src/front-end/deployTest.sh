ssh root@thatsapp.io 'rm -r /srv/www/beta.thatsapp.io/*'
scp -rp dist/* root@thatsapp.io:/srv/www/beta.thatsapp.io/

