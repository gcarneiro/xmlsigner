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

#### Faça o download dos arquivos

Caso já tenha feito o download dos arquivos e deseje apenas sincronizar aqui com o `gitbub` faça:

```
cd /usr/local/bin/xmlsigner
git pull origin master
```

#### Baixando pela 1ª vez:

O diretório de instalação será `/usr/local/bin/xmlsigner`

Execute:

```
cd /usr/local/bin
git clone https://github.com/gcarneiro/xmlsigner.git
cd xmlsigner
chmod +x xmlsigner
```

#### Se precisar compilar o script em Java execute:

```
cd /usr/local/bin/xmlsigner
rm sign.class
apt install build-essential
make
```

#### Configurações

No arquivo `/home/usuário/.bashrc` de cada usuário insira a linha:

```
export PATH=/usr/local/bin/xmlsigner:$PATH
```

No arquivo `/etc/skel/.bashrc` insira a linha no final:

```
if [ -d "/usr/local/bin/xmlsigner" ] ; then
    PATH=/usr/local/bin/xmlsigner:$PATH
fi
```

E assim sendo terá o comando `xmlsigner` disponível na linha de comando.