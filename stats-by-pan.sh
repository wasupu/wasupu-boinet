#!/usr/bin/env bash
set -o errexit
set -o pipefail
[[ "${DEBUG}" == 'true' ]] && set -o xtrace

if [[ $# < 2 ]]; then
    echo "Usage: ./stats.sh <FILE> <PAN>"
    exit 1
fi
FILE=$1
PAN=$2

echo "balances:"
grep --regexp="\"pan\":\"${PAN}\".*\"balance\"" ${FILE} | docker run -i stedolan/jq@sha256:a61ed0bca213081b64be94c5e1b402ea58bc549f457c2682a86704dd55231e09 -r '"\(.date) - \(.balance)"'

PRODUCT_TYPES=("mortgage" "power_supply" "water_supply" "meal" "entertainment" "internet" "gas" "medical_costs" "car_fault" "public_transport" "electronic_device" "holidays" "luxury" "new_car")
for productType in "${PRODUCT_TYPES[@]}"
do
    echo "${productType}:" $(grep --regexp="\"pan\":\"${PAN}\".*\"details\":\"${productType}\"" ${FILE} | wc -l)
done
