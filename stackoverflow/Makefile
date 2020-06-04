create_db:
	python manage.py sqlcreate | sudo -u postgres psql
	echo "create extension pgcrypto" | sudo -u postgres psql

shell:
	poetry install
	python manage.py migrate
	python manage.py shell_plus

run:
	poetry install
	python manage.py migrate
	python manage.py runserver 

reset_db:
	python manage.py reset_db
