#!/bin/sh

echo "Implementing $FILE on gandalf and legolas..."
scp $1 getin@gandalf.getin.mx:/usr/local/allshoppings/etc/
scp $1 getin@legolas.getin.mx:/usr/local/allshoppings/etc/

