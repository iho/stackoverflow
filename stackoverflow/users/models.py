import hashlib

from django.contrib.auth.models import AbstractUser
from django.db import models


class User(AbstractUser):
    bio = models.TextField(max_length=500, blank=True)
    location = models.CharField(max_length=30, blank=True)
    birth_date = models.DateField(null=True, blank=True)
    avatar = models.ImageField(upload_to='avatars', blank=True, null=True)
    use_avatar = models.BooleanField(default=True)

    class Meta:
        db_table = "users"

    def get_avatar(self):
        if self.use_avatar and self.avatar:
            return self.avatar.url
        email_hash = hashlib.md5(self.email.lower()).hexdigest()
        url = 'http://www.gravatar.com/avatar/%s?s=200&d=identicon' % email_hash
        return url

    def __str__(self) -> str:
        return self.username