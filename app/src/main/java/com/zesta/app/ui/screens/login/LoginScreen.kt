package com.zesta.app.ui.screens.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.LinkTextStyle
import com.zesta.app.ui.theme.PlaceholderShape
import com.zesta.app.ui.theme.ZestaBackground
import com.zesta.app.ui.theme.ZestaPlaceholder
import com.zesta.app.ui.theme.ZestaTextPrimary

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZestaBackground)
            .padding(horizontal = 28.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = stringResource(R.string.login_welcome),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_zesta),
            contentDescription = stringResource(R.string.login_logo_description),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Fit
        )


        Spacer(modifier = Modifier.height(42.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.login_email),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = PlaceholderShape,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ZestaPlaceholder,
                unfocusedContainerColor = ZestaPlaceholder,
                disabledContainerColor = ZestaPlaceholder,
                focusedBorderColor = ZestaPlaceholder,
                unfocusedBorderColor = ZestaPlaceholder,
                cursorColor = ZestaTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.login_password),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = PlaceholderShape,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ZestaPlaceholder,
                unfocusedContainerColor = ZestaPlaceholder,
                disabledContainerColor = ZestaPlaceholder,
                focusedBorderColor = ZestaPlaceholder,
                unfocusedBorderColor = ZestaPlaceholder,
                cursorColor = ZestaTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryGradientButton(
            text = stringResource(R.string.login_sign_in),
            onClick = onLoginSuccess
        )

        Spacer(modifier = Modifier.height(18.dp))

        PrimaryGradientButton(
            text = stringResource(R.string.login_sign_up),
            onClick = onGoRegister
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { }) {
            Text(
                text = stringResource(R.string.login_forgot_password),
                style = LinkTextStyle
            )
        }

        TextButton(onClick = { }) {
            Text(
                text = stringResource(R.string.login_continue_guest),
                style = LinkTextStyle
            )
        }
    }
}
