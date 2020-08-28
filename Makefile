export CLASSPATH=itext-2.0.4.jar:sign.class:.

all: sign.class

sign.class: sign.java
	javac sign.java

test:
	java sign certificado.pfx SENHA arquivo.pdf arquivo_assinado.pdf
