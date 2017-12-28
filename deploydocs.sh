#!/bin/bash
sudo rm -fr /var/www/html/procedures/systemdocs/*
sudo cp -r /home/getin/apidocs/* procedures/systemdocs/
sudo chmod -R o+rx /var/www/html/procedures/systemdocs/*

