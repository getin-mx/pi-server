#!/bin/sh

memcached -d -u mhapanowicz -o slab_reassign,slab_automove,lru_crawler,lru_maintainer,maxconns_fast,hash_algorithm=murmur3

