#verificando se arquivo foi executado em modo root
if [ "$EUID" -ne 0 ]; then
    echo "use: sudo bash install.sh"
    exit
fi

#atualizando sistema
clear
sudo apt-get update
clear
sudo apt-get upgrade -y
clear
echo -e "\n[!] \033[01;32mSISTEMA ATUALIZADO .... ✔️ \033[01;37m\n"
sleep 3

#instalando java
clear
sudo apt-get install openjdk-19-jdk -y
clear
echo -e "\n[!] \033[01;32mJAVA INSTALADO .... ✔️ \033[01;37m\n"
sleep 3

#install Arduino IDE
# 64bits
qq=`file /bin/bash | awk {'print $3'}`

if [ $qq == "64-bit" ]; then 
    wget https://downloads.arduino.cc/arduino-1.8.19-linux64.tar.xz?_gl=1*g7h2ce*_ga*NzQyOTEwOTYwLjE2ODI3MDEzMTA.*_ga_NEXN8H46L5*MTY4MjcwMTMwOS4xLjEuMTY4MjcwMzg0NS4wLjAuMA.. -O arduino-1.8.19-linux64.tar.xz
    clear
    exe=`ls | grep arduino-1.8.19-linux64.tar.xz`
    if [ $? == "0" ]; then
        echo -e "\n[!] \033[01;32mBaixado .... ✔️ \033[01;37m\n"
        sleep 5
        echo -e "\n[!] \033[01;32mCompactando Arduino IDE... \033[01;37m\n"
        sleep 5
        tar -xvf arduino-1.8.19-linux64.tar.xz
        clear
        sleep 3
        echo -e "\n[!] \033[01;32mDescompactado .... ✔️ \033[01;37m\n"
        sleep 4
        echo -e "\n[!] \033[01;32mInstalando Arduino IDE.... ✔️ \033[01;37m\n"
        sudo bash arduino-1.8.19/install.sh
        sleep 3 
        clear
        echo -e "\n[!] \033[01;32mArduino IDE instalado .... ✔️ \033[01;37m\n"
        exit
    fi
#32 bits
elif [ $qq == "32-bit" ]; then
    wget https://downloads.arduino.cc/arduino-1.8.19-linux32.tar.xz?_gl=1*zow6a1*_ga*NzQyOTEwOTYwLjE2ODI3MDEzMTA.*_ga_NEXN8H46L5*MTY4MjcwMTMwOS4xLjEuMTY4MjcwMzg0NS4wLjAuMA.. -O arduino-1.8.19-linux32.tar.xz
    clear
    exe=`ls | grep arduino-1.8.19-linux32.tar.xz`
    if [ $? == "0" ]; then
        echo -e "\n[!] \033[01;32mBaixado .... ✔️ \033[01;37m\n"
        sleep 3
        tar -xvf arduino-1.8.19-linux32.tar.xz
        clear
        sleep 3
        echo -e "\n[!] \033[01;32mDescompactado .... ✔️ \033[01;37m\n"
        sudo bash arduino-1.8.19/install.sh
        sleep 4 
        clear
        echo -e "\n[!] \033[01;32mArduino IDE instalado .... ✔️ \033[01;37m\n"
        exit
    fi
fi


