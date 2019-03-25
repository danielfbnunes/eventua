# Update - EventUA
6 de janeiro de 2018

Este README serve de suporte às alterações efetuadas na app EventUA, após diversas sugestões do professor regente da cadeira de Introdução à Computação Móvel.

## Limitações Ultrapassadas

* Possibilidade de fornecer informação privilegiada aos utilizadores que se encontrem no espaço de um determinado evento (questionários, documentos, etc...) através do uso de beacons;
* Search Provider, para uma maior facilidade na procura sobre qualquer informação dentro de toda a aplicação, isto é, permitir a pesquisa de eventos, participantes, oradores e palestras, na mesma barra de pesquisa.

### Beacons

Os Beacons estão a ser utilizados para fornecer informação priviligiada a um utilizador que se encontre num determinado espaço.
Desta forma, sempre que uma palestra termina, um utilizador que se encontre no local da mesma poderá ter acesso a uma área de avaliação, onde poderá classificá-la de 1 a 5 e adicionar comentários à mesma.
Através da utilização de beacons, restringe-se a avaliação de uma palestra/workshop apenas a participantes que tenham participado na mesma.

#### Configurações

Para a correta utilização dos beacons são necessários os seguintes passos:
* **Para o utilizador:** Fornecer permissões de localização e posteriormente ligar a localização do dispositivo assim como o bluetooth;
* **Para o programador:** Adicionar os beacons ao firebase criando uma “instância” para cada um e colocando lá o seu MAC address assim como o evento e a palestra a que cada beacon está associado.

![](https://i.ibb.co/WxjGVTv/beacons.png)

Com isto, passados alguns segundos, a aplicação comunicará com o beacon mais perto do dispositivo (neste caso, obrigatoriamente com uma distância inferior a 1 metro, caso contrário, são descartados) e irá aparecer um formulário para fornecer o rating e um comentário da palestra a que o beacon está associado.
Simula-se assim a possibilidade de dar feedback sobre uma palestra a quem se encontre a assistir à mesma.

### Global Search Provider

Sempre que um utilizador adquire o bilhete dum evento, este passa a ter acesso ao Global Search Provider do mesmo. 
Aqui, o utilizador poderá pesquisar por oradores, participantes e palestras, tudo no mesmo local. Isto permite uma pesquisa mais cómoda e eficiente, permitindo facilmente ao utilizador que encontre o conteúdo que procura.

## Interface da Aplicação

![](https://i.ibb.co/5G84R91/Screenshot-2019-01-05-at-10-43-10.png)
![](https://i.ibb.co/wp53cwx/Screenshot-2019-01-05-at-10-43-45.png)

## Instalação
   - Apenas será necessário instalar o [EventUA.apk](http://code.ua.pt//projects/eventua_84793_84921/files) no dispositivo.

### Autores
* **Daniel Nunes** - 84793 - Engenharia Informática - Universidade de Aveiro
* **Rafael Direito** - 84921 - Engenharia Informática - Universidade de Aveiro



