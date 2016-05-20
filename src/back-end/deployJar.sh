scp build/libs/whatistics-back-end-all.jar root@thatsapp.io:/root/
ssh root@thatsapp.io 'docker stop thatsapp && docker rm thatsapp'
ssh root@thatsapp.io '/root/startThatsApp.sh'
