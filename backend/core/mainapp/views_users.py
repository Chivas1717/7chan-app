# mainapp/views_users.py
from rest_framework.generics import RetrieveAPIView
from django.contrib.auth.models import User
from rest_framework.permissions import AllowAny

from .serializers import UserProfileSerializer


class UserProfileView(RetrieveAPIView):
    permission_classes = [AllowAny]
    queryset = User.objects.all()
    serializer_class = UserProfileSerializer
