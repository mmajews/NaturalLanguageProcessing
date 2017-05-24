#!/usr/bin/env bash
grep -n -w -A $1 -B $1 $2 in/potop.txt