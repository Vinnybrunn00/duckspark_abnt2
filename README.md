## DUCKSPARK ABNT2 💾
Este projeto é capaz de converter scripts ```.duck``` para scripts Arduino ```.ino``` suportando teclado ABNT2 e compilar no Digispark.

## INSTALANDO DEPENDÊNCIAS NO WINDOWS
- Você precisará instalar o [Java](https://www.java.com/pt-BR/download/ie_manual.jsp?locale=pt_BR) e o [python](https://www.python.org/downloads/).
- Baixe a IDE do Arduino [aqui](https://www.arduino.cc/en/software), para ser possível compilar o arquivo ```.ino``` gerado.

## INSTALANDO DEPENDÊNCIAS NO LINUX
Execute o arquivo ```ìnstall.sh``` pra instalar as dependências.
```bash
> sudo bash install.sh
```
## CONVERTENDO ARQUIVOS COMPATÍVEIS
- Esse processo converte um script ```.duck``` válido para um script Arduino capaz de ser compilado no Digispark

Primeiro, converta o seu script [.duck]() ```payload.duck``` para um arquivo binário ```payload.bin``` com este comando:
```bash
> java -jar encoder/encoder.jar -i payload.duck -o payload.bin -l encoder/resources/br.properties
```

Despois, converta o arquivo binário ```payload.bin``` salvo na pasta para o arquivo ```payload.ìno```:
```bash
> python duckspark/duckspark.py -i payload.bin -l 1 -f 2000 -o payload.ino 
```
### Pronto!
- Ao gerar o arquivo ```payload.ino``` na pasta, abra o arquivo na IDE do Arduino e compile no Digispark

Siga o [tutorial](https://www.robocore.net/tutoriais/primeiros-passos-digispark-attiny85) de como configurar e compilar no Arduino IDE e deixa-lo totalmente funcional.