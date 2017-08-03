#!/bin/sh

FILE=`basename $1`
echo "Implementing $FILE on sam..."
scp /tmp/allshoppings/lib/$FILE getin@sam.getin.mx:/usr/local/allshoppings/lib/
scp /tmp/allshoppings/lib/$FILE getin@gandalf.getin.mx:/usr/local/getin/lib/
scp /tmp/allshoppings/lib/$FILE getin@legolas.getin.mx:/usr/local/getin/lib/

