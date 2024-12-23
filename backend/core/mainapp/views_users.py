# mainapp/views_users.py
from rest_framework.generics import RetrieveAPIView
from django.contrib.auth.models import User
from .serializers import UserProfileSerializer


class UserProfileView(RetrieveAPIView):
    queryset = User.objects.all()
    serializer_class = UserProfileSerializer
