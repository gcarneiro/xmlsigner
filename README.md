# Assinatura de XML's com Certificado Digital PFX

### Verificar se o `java` está instalado

```
java -version
openjdk version "11.0.4" 2019-07-16
OpenJDK Runtime Environment (build 11.0.4+11-post-Ubuntu-1ubuntu219.04)
OpenJDK 64-Bit Server VM (build 11.0.4+11-post-Ubuntu-1ubuntu219.04, mixed mode, sharing)
```

### Verificar se o `openssl` está instalado

```
openssl version
OpenSSL 1.1.1b  26 Feb 2019
```

### INSTALAÇÃO: 

#### Baixando pela 1ª vez:

O diretório de instalação deve ser /usr/local/bin/pdfsigner para que seja possível a execução normal

Execute:

```
cd /usr/local/bin/
git clone https://github.com/gcarneiro/xmlsigner.git
cd xmlsigner
chmod +x xmlsigner
```

Após a instalação vamos criar um link simbolico para que qualquer usuário possa utilizar

```
ln -s /usr/local/bin/xmlsigner/xmlsigner /usr/bin/xmlsigner
```


#### Se precisar compilar o script em Java execute:

```
cd /usr/local/bin/xmlsigner
rm sign.class
apt install build-essential
make
```
