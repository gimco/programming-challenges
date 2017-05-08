#!/bin/bash

REPO=/tmp/repo
cd $REPO

SCRIPTDIR=/tmp/kk
if [ ! -d $SCRIPTDIR ]; then
	mkdir $SCRIPTDIR
	find .git/objects/ \
		| egrep '[0-9a-f]{38}' \
		| perl -pe 's:^.*([0-9a-f][0-9a-f])/([0-9a-f]{38}):\1\2:' \
		| \
	while read hash ; do
		if [ "$(git cat-file -t $hash)" == "commit" ]; then
			DATE=$(git show -s --format=%ci $hash)
			DATE=${DATE:0:10}

			echo $DATE
			git show $hash:script.php > $SCRIPTDIR/$DATE.php
		fi
	done

	# Optimizar scripts cambiando bucles por exponenciación modular rápida
	cd $SCRIPTDIR
	sed -i "" -e 's#for ($i=0; $i<10000000; $i++)#$counter = ($secret3 * bcpowmod($secret1, 10000000, $secret2)) % $secret2; if (false)#' *
fi


function regenerate {
	ID=$1
	DATE=$2
	CHANGES=$3
	HASH=$4

	SCRIPT=$SCRIPTDIR/$DATE.php
	#REV=$(git rev-list -n 1 --after="$DATE 00:00:00" --before="$DATE 23:59:59" --all)
	#git checkout $REV </dev/null &>/dev/null

	for x in $(seq $CHANGES); do
		if [ -z "$HASH" ]; then
			RESULT=$(php $SCRIPT $ID)
		else
			RESULT=$(php $SCRIPT $ID $HASH)
		fi
		echo $RESULT >> /tmp/l.txt
		PASSWORD=${RESULT:0:10}
		HASH=${RESULT:10}
	done
	echo $RESULT
}


read CASES

for c in $(seq $CASES); do
	read -a CONFIG
	ID=${CONFIG[0]}
	HISTORY=${CONFIG[1]}

	HASH=""
	for n in $(seq $HISTORY); do
		read -a ENTRY
		DATE=${ENTRY[0]}
		CHANGES=${ENTRY[1]}

		RESULT=$(regenerate $ID $DATE $CHANGES $HASH)
		PASSWORD=${RESULT:0:10}
		HASH=${RESULT:10}
	done
	printf "Case #%d: %s\n" $c $PASSWORD
done




#git rev-list -n 1 --after="2012-07-24 00:00" --before="2012-07-24 23:59:59" master