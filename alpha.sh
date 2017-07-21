#!/bin/sh

FILE=`basename $1`
echo "Implementing $FILE on sam..."
scp /tmp/allshoppings/lib/$FILE getin@sam.getin.mx:/usr/local/allshoppings/lib/

