#!/usr/bin/env bash
set -o errexit
set -o pipefail
[[ "${DEBUG}" == 'true' ]] && set -o xtrace

if [[ $# < 1 ]]; then
    echo "Usage: ./stats.sh <FILE>"
    exit 1
fi
FILE=$1

PRODUCT_TYPES=("mortgage" "power_supply" "water_supply" "meal" "entertainment" "internet" "gas" "medical_costs" "car_fault" "public_transport" "with_devices" "vacations" "luxury" "new_car")
for productType in "${PRODUCT_TYPES[@]}"
do
    echo "${productType}:" $(grep --regexp="\"details\":\"${productType}\"" ${FILE} | wc -l)
done
