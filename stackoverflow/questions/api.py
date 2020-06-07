from rest_framework import serializers, viewsets

from questions.models import Question, Answer, AnswerVote, QuestionVote, Category
from questions.serializers import QuestionSerializer, QuestionVoteSerializer, AnswerSerializer, AnswerVoteSerializer, CategorySerializer
from questions.permissions import IsOwnerOrReadOnlyPermission
from django_filters.rest_framework import DjangoFilterBackend

class QuestionViewSet(viewsets.ModelViewSet):
    queryset = Question.objects.all()
    serializer_class = QuestionSerializer
    permission_classes = [IsOwnerOrReadOnlyPermission]
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['id', 'user', 'category']

class QuestionVoteViewSet(viewsets.ModelViewSet):
    queryset = QuestionVote.objects.all()
    serializer_class = QuestionVoteSerializer
    permission_classes = [IsOwnerOrReadOnlyPermission]
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['id', 'question', 'user']

class AnswerViewSet(viewsets.ModelViewSet):
    queryset = Answer.objects.all()
    serializer_class = AnswerSerializer
    permission_classes = [IsOwnerOrReadOnlyPermission]
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['id', 'user', 'question']


class AnswerVoteViewSet(viewsets.ModelViewSet):
    queryset = AnswerVote.objects.all()
    serializer_class = AnswerVoteSerializer
    permission_classes = [IsOwnerOrReadOnlyPermission]
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['id', 'user', 'answer']


class CategoryViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Category.objects.all()
    serializer_class = CategorySerializer
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['id', 'slug']