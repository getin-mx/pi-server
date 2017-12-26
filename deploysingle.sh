#!/bin/bash
ssh -t -i $1 getinit@$2.getin.mx "/usr/local/allshoppings/bin/killserver.sh"
ssh -t -i $1 getinit@$2.getin.mx "rm -f /usr/local/allshoppings/lib/*"
scp -r -i $1 /tmp/allshoppings/lib getinit@$2.getin.mx:/usr/local/allshoppings
ssh -t -i $1 getinit@$2.getin.mx "/usr/local/allshoppings/bin/asrestart"