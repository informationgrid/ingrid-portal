#while ! curl --http0.9 -o - localhost:3306 &> /dev/null; do echo "waiting for DB"; sleep 1; done

echo "Creating databases if not already existing"
mysql -u root -h 127.0.0.1 -e "CREATE DATABASE IF NOT EXISTS mdek"
# mysql -u root -h 127.0.0.1 -e "CREATE DATABASE IF NOT EXISTS \`igc_test\`" # created automatically
mysql -u root -h 127.0.0.1 -e "CREATE DATABASE IF NOT EXISTS \`ingrid_portal\`"