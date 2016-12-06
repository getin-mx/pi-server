#!/bin/sh

FILE=`basename $1`
echo "Implementing $FILE on gandalf and legolas..."
scp /tmp/allshoppings/lib/$FILE getin@gandalf.getin.mx:/usr/local/allshoppings/lib/
scp /tmp/allshoppings/lib/$FILE getin@legolas.getin.mx:/usr/local/allshoppings/lib/

