from django.db import models
from django.urls import reverse
from django.utils.translation import ugettext_lazy as _
from django.contrib.auth import get_user_model


User = get_user_model()


class Category(models.Model):
    name = models.CharField(max_length=80, unique=True)
    slug = models.SlugField(_('Slug'), max_length=60, unique=True)

    def get_absolute_url(self):
        return reverse('cat', args=[str(self.slug)])

    class Meta:
        verbose_name = _('Category')
        verbose_name_plural = _('Categorys')

    def __str__(self):
        return self.name


class QuestionVote(models.Model):
    DOWNVOTE = -1
    UPVOTE = 1
    VOTE_CHOICES = (
        (DOWNVOTE, 'Downvote'),
        (UPVOTE, 'Upvote'),
    )
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    question = models.ForeignKey('Question', on_delete=models.CASCADE)
    created = models.DateTimeField(verbose_name=_('Crated'), auto_now_add=True)
    raiting = models.SmallIntegerField(default=UPVOTE, choices=VOTE_CHOICES)

    class Meta:
        unique_together = ('user', 'question')


class Question(models.Model):    
    name = models.CharField(_('Name'), max_length=60)
    slug = models.SlugField(_('Slug'), max_length=60)
    text = models.TextField(_('Question'))
    updated = models.DateTimeField(verbose_name=_('Updated'), auto_now=True)
    created = models.DateTimeField(verbose_name=_('Crated'), auto_now_add=True)
    user = models.ForeignKey(User, verbose_name=_('Owner'), on_delete=models.CASCADE)
    raiting = models.PositiveIntegerField(_('Raiting'), default=0)
    voters = models.ManyToManyField(User, verbose_name=_('Voted'), related_name='voted_questions', through='QuestionVote')
    category = models.ForeignKey(Category, verbose_name=_('Category'), blank=False, null=True, on_delete=models.SET_NULL)

    class Meta:
        verbose_name = _('Question')
        verbose_name_plural = _('Questions')

    def __str__(self):
        return self.name


class AnswerVote(models.Model):
    DOWNVOTE = -1
    UPVOTE = 1
    VOTE_CHOICES = (
        (DOWNVOTE, 'Downvote'),
        (UPVOTE, 'Upvote'),
    )
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    answer = models.ForeignKey('Answer', on_delete=models.CASCADE)
    created = models.DateTimeField(verbose_name=_('Crated'), auto_now_add=True)
    raiting = models.SmallIntegerField(default=UPVOTE, choices=VOTE_CHOICES)

    class Meta:
        unique_together = ('user', 'answer')


class Answer(models.Model):
    question = models.ForeignKey(Question, related_name='answers', verbose_name=_('Question'), on_delete=models.CASCADE)
    text = models.TextField(verbose_name=_('Text'))
    updated = models.DateTimeField(verbose_name=_('Updated'), auto_now=True)
    created = models.DateTimeField(verbose_name=_('Crated'), auto_now_add=True)
    user = models.ForeignKey(User, blank=False, verbose_name=_('User'), on_delete=models.CASCADE)
    voters = models.ManyToManyField(User, verbose_name=_('Voted'), related_name='voted_answers', through='AnswerVote')
    raiting = models.PositiveIntegerField(_('Raiting'), default=0)

    def __str__(self):
        return str(self.created)

    class Meta:
        ordering = ['-created']
        get_latest_by = 'created'
        verbose_name = _('Answer')
        verbose_name_plural = _('Answers')