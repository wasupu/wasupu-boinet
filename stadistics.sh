#!/usr/bin/env bash

FILE=$1

echo "mortgages:" $(cat $FILE | grep mortgage | wc -l)
echo "power_supply:" $(cat $FILE | grep power_supply | wc -l)
echo "water_supply:" $(cat $FILE | grep water_supply | wc -l)
echo "meal:" $(cat $FILE | grep meal | wc -l)
echo "entertainment:" $(cat $FILE | grep entertainment | wc -l)
echo "internet:" $(cat $FILE | grep internet | wc -l)
echo "gas:" $(cat $FILE | grep gas | wc -l)
echo "medical_costs:" $(cat $FILE | grep medical_costs | wc -l)
echo "car_fault:" $(cat $FILE | grep car_fault | wc -l)
echo "public_transport:" $(cat $FILE | grep public_transport | wc -l)
echo "with_devices:" $(cat $FILE | grep device | wc -l)
echo "vacations:" $(cat $FILE | grep vacations | wc -l)
echo "luxury:" $(cat $FILE | grep luxury | wc -l)
echo "new_car:" $(cat $FILE | grep new_car | wc -l)