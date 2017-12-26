#!/bin/bash
rm -fr target/site/apidocs/*
mvn javadoc:aggregate
ssh -t -i /media/sf_VBox_Shared/.ssh/han.ppk getin@leia.getin.mx "rm -fr /home/getin/apidocs"
scp -r -i /media/sf_VBox_Shared/.ssh/han.ppk target/site/apidocs getin@leia.getin.mx:/home/getin
ssh -t -i /media/sf_VBox_Shared/.ssh/han.ppk getin@leia.getin.mx "/home/getin/deploydocs.sh"
