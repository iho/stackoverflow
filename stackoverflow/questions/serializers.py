from rest_framework import serializers

from questions.models import Question, Answer, AnswerVote, QuestionVote, Category

class QuestionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Question
        fields = (
            'id', 
            'name',
            'slug', 
            'text',
            'created',
            'updated',
            'user', 
            'raiting', 
            'category',
        )


class QuestionVoteSerializer(serializers.ModelSerializer):
    class Meta:
        model = QuestionVote
        fields = (
            'id', 
            'question',
            'raiting',
        )


class AnswerSerializer(serializers.ModelSerializer):
    class Meta:
        model = Answer
        fields = (
            'id', 
            'question',
            'text',
            'created',
            'updated',
            'user', 
            'raiting', 
        )


class AnswerVoteSerializer(serializers.ModelSerializer):
    class Meta:
        model = AnswerVote
        fields = (
            'id', 
            'answer',
            'raiting',
        )

class CategorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Category
        fields = ('id', 'name', 'slug', 'description')