#!/bin/bash

rm -rf /tmp/msgsend_*

#Get the Date and Time for naming the file
date=/tmp/msgsend_$(date +"%d-%h-%Y_%I:%M:%s_%p").txt

# Get the column positions
number_col=$(head -n 1 data.csv | tr ',' '\n' | cat -n | grep 'number' | awk '{print $1}')
money_col=$(head -n 1 data.csv | tr ',' '\n' | cat -n | grep 'money' | awk '{print $1}')

# Extract the values and format the output
tail -n +2 data.csv | while read line; do
    number=$(echo "$line" | cut -d ',' -f "$number_col" | tr -d '-')
    money=$(echo "$line" | cut -d ',' -f "$money_col")
    echo "curl -X POST https://api.twilio.com/2010-04-01/Accounts/AC2a1d2545d7c4388bce7f8cebf8bb5641/Messages.json \\\n"
        		+ "--data-urlencode \"To=+1" + number + "\" \\"+"\n"
        		+ "--data-urlencode \"From=+15613635561\" \\\n"
        		+ "--data-urlencode \"Body=You have an outstanding balance of $ "+money+" with Allergy Consultant Inc.To make a payment via Phone or if you have any questions, Please call us at 5613687006. Reply STOP to optout.\" \\\n"
        		+ "-u \"AC2a1d2545d7c4388bce7f8cebf8bb5641:06c980d9b27e54e18952a05a611485bb\"\n" >> $date
declare -i count=0
count=$((count+1))
done
    echo "Total Messages $count"
    cat $date
    echo "*************************************************COMPLETED*************************************************"
    
