#!/usr/bin/python
import sys
import getopt
import os

def generate_source(payload, init_delay=2500, loop_count=-1, loop_delay=5000, blink=True):
	head = '''/*
* Esboço gerado por duck2spark de Marcus Mengs(mama82) - "https://github.com/mama82"
*
*/
#include "DigiKeyboard.h"

'''
	init = '''
void setup()
{
	// initialize the digital pin as an output.
	pinMode(0, OUTPUT); //LED on Model B
	pinMode(1, OUTPUT); //LED on Model A
	DigiKeyboard.delay(%d); //wait %d milliseconds before first run, to give target time to initialize
}

void loop()
{
''' % (init_delay, init_delay)

	body = '''
	//should code be runned in this loop?
	if (i != 0) {
		DigiKeyboard.sendKeyStroke(0);

		//parse raw duckencoder script
		for (int i=0; i<DUCK_LEN; i+=2)
		{
			uint8_t key = pgm_read_word_near(duckraw + i);
			uint8_t mod = pgm_read_word_near(duckraw + i+1);
			if (key == 0) //delay (a delay>255 is split into a sequence of delays)
			{
				DigiKeyboard.delay(mod);
			}
			else DigiKeyboard.sendKeyStroke(key,mod);
		}
		i--;
		DigiKeyboard.delay(%d); //wait %d milliseconds before next loop iteration

	}
	else if (blink)
	{
		digitalWrite(0, HIGH);   // turn the LED on (HIGH is the voltage level)
		digitalWrite(1, HIGH);
		delay(100);               // wait for a second
		digitalWrite(0, LOW);    // turn the LED off by making the voltage LOW
		digitalWrite(1, LOW);
		delay(100);               // wait for a second
	}
''' % (loop_delay, loop_delay)

	tail = '''}
'''
	l = len(payload)
	# payload into FLASH memory of digispark
	declare = "#define DUCK_LEN " + str(l) + "\nconst PROGMEM uint8_t duckraw [DUCK_LEN] = {\n\t"
	for c in range(l - 1):
		declare += str(hex(payload[c])) + ", "
	declare += str(hex(payload[l - 1])) + "\n};\nint i = %d; //how many times the payload should run (-1 for endless loop)\n" % loop_count
	if blink:
		declare += "bool blink=true;\n"
	else:
		declare += "bool blink=false;\n"

	return head + declare + init + body + tail

def usage():
	usagescr = '''MaMe82 duck2spark 1.0
=====================

Converte a carga útil criada pelo DuckEncoder em arquivo de origem para o DigiSpark Sketch

Uso: python duck2spark -i [file ..]			Construa o Sketch a partir do arquivo de carga útil especificado do RubberDucky
   ou: python duck2spark -i [file ..] -o [file ..]	Salvar a origem do esboço no arquivo de saída especificado

Argumentos:
   -i [file ..] 		Arquivo de entrada (Payload ecnoded com DuckEncoder)
   -o [file ..] 		Arquivo de saída para esboço, se stdout omitido for usado
   -l <count> 			Contagem de loops (1=single run (default), -1=endless run, 3=3 runs etc.)
   -f <millis> 			Atraso em milissegundos antes da execução inicial do payload (default 1000)
   -r <millis> 			Atraso em milissegundos entre execuções de loop (default 5000)
   -n				Não pisque o LED de status após o término da execução da carga útil

Observação:	Para usar DEAD KEYS (f.e. ^ and ` no layout de teclado alemão) um ESPAÇO deve ser
	anexado no script ducky (f.e. "STRING ^ working deadkey").
'''
	print(usagescr)

def main(argv):
	ifile = ""
	ofile = None
	payload = None
	loop_count = 1
	blink = True
	init_delay = 1000
	loop_delay = 5000
	try:
		opts, args = getopt.getopt(argv, "hi:o:l:nf:r:", ["help", "input=", "output=", "loopcount=", "noblink", "initdelay=", "repeatdelay="])
	except getopt.GetoptError:
		usage()
		sys.exit(2)
	for opt, arg in opts:
		if opt in ("-h", "--help"):
			usage()
			sys.exit()
		elif opt == '-d':
			global _debug
			_debug = 1
		elif opt in ("-i", "--input"):
			ifile = arg
			if not os.path.isfile(ifile) or not os.access(ifile, os.R_OK):
				print("Arquivo de entrada " + ifile + " não existe ou não é legível")
				sys.exit(2)
			with open(ifile, "rb") as f:
				payload = f.read()
		elif opt in ("-o", "--output"):
			ofile = arg
		elif opt in ("-l", "--loopcount"):
			loop_count = int(arg)
		elif opt in ("-f", "--initdelay"):
			init_delay = int(arg)
		elif opt in ("-r", "--repeatdelay"):
			loop_delay = int(arg)
		elif opt in ("-n", "--noblink"):
			blink = False
	if payload is None:
		print("Você deve fornecer uma carga útil gerada pelo DuckEncoder (-i option)")
		sys.exit(2)

	# generate source code for Sketch
	result = generate_source(payload, init_delay=init_delay, loop_count=loop_count, loop_delay=loop_delay, blink=blink)

	if ofile is None:
		# print to stdout
		print(result)
	else:
		# write to ofile
		with open(ofile, "w") as f:
			f.write(result)

if __name__ == "__main__":
	if len(sys.argv) < 2:
		usage()
		sys.exit()
	main(sys.argv[1:])
