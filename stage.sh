#!/bin/sh

FILE=`basename $1`
echo "Implementing $FILE on gimli..."
scp /tmp/allshoppings/lib/$FILE getin@gimli.getin.mx:/usr/local/allshoppings/lib/

