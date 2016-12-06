#!/bin/sh

ssh root@doppler.allshoppings.mobi killall java
ssh root@faraday.allshoppings.mobi killall java
ssh root@doppler.allshoppings.mobi server-check
ssh root@faraday.allshoppings.mobi server-check
