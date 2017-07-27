#!/usr/bin/make -f

USER    ?= smartscale
HOME    ?= /srv/smartscale

build:
	$(MAKE) -C src

install: art build
	install -m 700 -o $(USER) -d $(HOME)/data
	install -m 755 -o root src/SmartScale.dex $(HOME)
	cp -r src/art $(HOME)
	chown -R $(USER):root $(HOME)/art
	chmod u+x $(HOME)/art/bin/art
	install -m 644 -o root ./src/smartscale@.service /etc/systemd/system
	install -m 644 -o root ./src/smartscale.socket /etc/systemd/system
	install -m 644 -o root ./src/system-smartscale.slice /etc/systemd/system
	systemctl enable smartscale.socket

art:
	./setup.sh
