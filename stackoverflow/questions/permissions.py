from rest_framework import permissions

class IsOwnerOrReadOnlyPermission(permissions.BasePermission):
    message = 'Adding customers not allowed.'

    def has_object_permission(self, request, view, obj):
        if request.method in permissions.SAFE_METHODS:
            return True
        return obj.user == request.user