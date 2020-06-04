from rest_framework import serializers

from questions.models import Question, Answer, AnswerVote, QuestionVote

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
            'owner', 
            'raiting', 
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
            'owner', 
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
