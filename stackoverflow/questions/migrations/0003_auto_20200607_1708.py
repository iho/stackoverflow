# Generated by Django 3.0.7 on 2020-06-07 17:08

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('questions', '0002_auto_20200604_2010'),
    ]

    operations = [
        migrations.AlterModelOptions(
            name='category',
            options={'verbose_name': 'Category', 'verbose_name_plural': 'Categories'},
        ),
        migrations.AddField(
            model_name='category',
            name='description',
            field=models.TextField(default=''),
            preserve_default=False,
        ),
        migrations.AlterField(
            model_name='answer',
            name='raiting',
            field=models.PositiveIntegerField(default=0, verbose_name='Raiting'),
        ),
        migrations.AlterField(
            model_name='question',
            name='raiting',
            field=models.PositiveIntegerField(default=0, verbose_name='Raiting'),
        ),
    ]
