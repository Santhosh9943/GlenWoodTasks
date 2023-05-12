#!/bin/bash

rm -rf /tmp/msgsend_*

#Get the Date and Time for naming the file
script_File_With_date=/tmp/msgsend_$(date +"%d-%h-%Y_%I:%M:%S_%p").sh

# Get the column positions
number_col=$(head -n 1 data.csv | tr ',' '\n' | cat -n | grep 'number' | awk '{print $1}')
money_col=$(head -n 1 data.csv | tr ',' '\n' | cat -n | grep 'money' | awk '{print $1}')

# Extract the values and format the output
tail -n +2 data.csv | while read line; do
    number=$(echo "$line" | cut -d ',' -f "$number_col" | tr -d '-')
    money=$(echo "$line" | cut -d ',' -f "$money_col")
    echo "curl -X POST https://api.twilio.com/2010-04-01/Accounts/AC2a1d2545d7c4388bce7f8cebf8bb5641/Messages.json \\">> $script_File_With_date
    echo "--data-urlencode \"To=+1$number\" \\n--data-urlencode \"From=+15613635561\" \\">> $script_File_With_date
    echo "--data-urlencode \"Body=You have an outstanding balance of \$ $money with Allergy Consultant Inc.To make a payment via Phone or if you have any questions, Please call us at 5613687006. Reply STOP to optout.\" \\" >> $script_File_With_date
    echo "-u \"AC2a1d2545d7c4388bce7f8cebf8bb5641:06c980d9b27e54e18952a05a611485bb\"" >> $script_File_With_date
    echo "\n" >> $script_File_With_date
#declare -i count=0
#count=$((count+1))
done
#    echo "Total Messages $count"
    gedit $script_File_With_date
    echo "\n*************************************************COMPLETED*************************************************\n"
    
