# -*- coding: utf-8 -*-
from django.contrib import admin

from .models import Category, QuestionVote, Question, AnswerVote, Answer


@admin.register(Category)
class CategoryAdmin(admin.ModelAdmin):
    list_display = ('id', 'name', 'slug')
    search_fields = ('name', 'slug')
    prepopulated_fields = {'slug': ['name']}


@admin.register(QuestionVote)
class QuestionVoteAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'question', 'created', 'raiting')
    list_filter = ('user', 'question', 'created')


@admin.register(Question)
class QuestionAdmin(admin.ModelAdmin):
    list_display = (
        'id',
        'name',
        'slug',
        'text',
        'updated',
        'created',
        'user',
        'raiting',
        'category',
    )
    list_filter = ('updated', 'created', 'user', 'category')
    raw_id_fields = ('voters',)
    search_fields = ('name', 'slug')
    prepopulated_fields = {'slug': ['name']}


@admin.register(AnswerVote)
class AnswerVoteAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'answer', 'created', 'raiting')
    list_filter = ('user', 'answer', 'created')


@admin.register(Answer)
class AnswerAdmin(admin.ModelAdmin):
    list_display = (
        'id',
        'question',
        'text',
        'updated',
        'created',
        'user',
        'raiting',
    )
    list_filter = ('question', 'updated', 'created', 'user')
    raw_id_fields = ('voters',)
